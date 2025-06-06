package top.aias.text2vec.utils;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * 向量模型
 *
 * @author Calvin
 * @mail 179209347@qq.com
 * @website www.aias.top
 */
public final class Text2VecModel implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(Text2VecModel.class);
    private ZooModel<String, float[]> model;
    Predictor<String, float[]> predictor;

    public Text2VecModel() throws IOException, ModelNotFoundException, MalformedModelException {

        Criteria<String, float[]> criteria =
                Criteria.builder()
                        .setTypes(String.class, float[].class)
                        .optModelPath(Paths.get("models/traced_embedding.pt"))
                        .optTranslator(new SentenceTranslator())
                        .optEngine("PyTorch") // Use PyTorch engine
                        .optDevice(Device.cpu())
                        .optProgress(new ProgressBar())
                        .build();

        model = ModelZoo.loadModel(criteria);
        predictor = model.newPredictor();
    }

    @Override
    public void close() {
        model.close();
        predictor.close();
    }

    public float[] encode(String input) throws TranslateException {
        return predictor.predict(input);
    }
}
