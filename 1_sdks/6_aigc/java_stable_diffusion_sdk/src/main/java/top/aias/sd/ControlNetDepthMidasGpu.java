package top.aias.sd;

import ai.djl.Device;
import ai.djl.ModelException;
import ai.djl.modality.cv.Image;
import ai.djl.opencv.OpenCVImageFactory;
import ai.djl.translate.TranslateException;
import top.aias.sd.pipelines.StableDiffusionControlNetPipeline;
import top.aias.sd.preprocess.depth.MidasDepthModel;
import top.aias.sd.utils.ImageUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ControlNetDepthMidasGpu {

    private ControlNetDepthMidasGpu() {}

    public static void main(String[] args) throws ModelException, IOException, TranslateException {
        Path imageFile = Paths.get("src/test/resources/depth.png");
        Image image = OpenCVImageFactory.getInstance().fromFile(imageFile);
        String prompt = "Stormtrooper's lecture in beautiful lecture hall";
        String modelPath = "models/midas_depth.pt";

        try (StableDiffusionControlNetPipeline model = new StableDiffusionControlNetPipeline("H:\\models\\aigc\\sd_gpu\\", "controlnet_depth.pt", Device.gpu());
             MidasDepthModel detector = new MidasDepthModel(512, 512, modelPath, 1, Device.gpu())) {
            Image depthImg = detector.predict(image);
            ImageUtils.saveImage(depthImg, "depthImg.png", "build/output");
            Image result = model.generateImage(depthImg, prompt, "", 25);
            ImageUtils.saveImage(result, "ctrlnet_midas_depth_pt_gpu.png", "build/output");
        }

    }
}