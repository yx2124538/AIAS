package top.aias.platform.model.preprocess.lineart;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import top.aias.platform.model.preprocess.ControlPool;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public final class LineArtAnimeModel implements AutoCloseable {
    private int detect_resolution = 512;
    private int image_resolution = 512;

    private ZooModel<Image, Image> model;
    private Device device;
    private ControlPool pool;
    private String modelPath;
    private int poolSize;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public LineArtAnimeModel(int detect_resolution, int image_resolution, String modelPath, int poolSize, Device device) {
        this.detect_resolution = detect_resolution;
        this.image_resolution = image_resolution;
        this.modelPath = modelPath;
        this.poolSize = poolSize;
        this.device = device;
    }

    public synchronized void ensureInitialized() {
        if (!initialized.get()) {
            try {
                this.model = ModelZoo.loadModel(criteria());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ModelNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedModelException e) {
                e.printStackTrace();
            }
            this.pool = new ControlPool(model, poolSize);
            initialized.set(true);
        }
    }

    public Image predict(Image img) throws TranslateException, ModelException, IOException {
        ensureInitialized();
        Predictor<Image, Image> predictor = pool.getPredictor();
        Image result = predictor.predict(img);
        pool.releasePredictor(predictor);
        return result;
    }

    public void close() {
        if (initialized.get()) {
            model.close();
            pool.close();
        }
    }

    private Criteria<Image, Image> criteria() {

        Criteria<Image, Image> criteria =
                Criteria.builder()
                        .optEngine("PyTorch")
                        .setTypes(Image.class, Image.class)
                        .optModelPath(Paths.get(modelPath)) // lineart_anime.pt
                        .optDevice(device)
                        .optTranslator(new FeatureTranslator())
                        .optProgress(new ProgressBar())
                        .build();

        return criteria;
    }

    private final class FeatureTranslator implements Translator<Image, Image> {
        protected Batchifier batchifier = Batchifier.STACK;
        private int width;
        private int height;

        FeatureTranslator() {
        }

        @Override
        public NDList processInput(TranslatorContext ctx, Image input) {
            width = input.getWidth();
            height = input.getHeight();

            NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);

            int Hn = 256 * (int) (Math.ceil((float) height / 256.0));
            int Wn = 256 * (int) (Math.ceil((float) width / 256.0));

            array = NDImageUtils.resize(array, Hn, Wn, Image.Interpolation.BICUBIC);

            array = array.transpose(2, 0, 1).div(127.5f).sub(1.0f); // HWC -> CHW RGB

            return new NDList(array);
        }

        @Override
        public Image processOutput(TranslatorContext ctx, NDList list) {
            NDManager manager = ctx.getNDManager();

            NDArray line = list.singletonOrThrow();

            line = line.mul(127.5f).add(127.5f);

            Image img = ImageFactory.getInstance().fromNDArray(line);
            line = NDImageUtils.resize(img.toNDArray(manager), width, height, Image.Interpolation.BICUBIC);
            line = line.clip(0, 255).toType(DataType.UINT8, false);

            int[] hw = resize64(height, width, image_resolution);
            line = NDImageUtils.resize(img.toNDArray(manager), hw[1], hw[0], Image.Interpolation.BILINEAR);
            line = line.neg().add(255);
            img = ImageFactory.getInstance().fromNDArray(line);

            return img;
        }


        private int[] resize64(double h, double w, double resolution) {

            double k = resolution / Math.min(h, w);
            h *= k;
            w *= k;

            int height = (int) (Math.round(h / 64.0)) * 64;
            int width = (int) (Math.round(w / 64.0)) * 64;

            return new int[]{height, width};
        }

        @Override
        public Batchifier getBatchifier() {
            return batchifier;
        }

    }
}
