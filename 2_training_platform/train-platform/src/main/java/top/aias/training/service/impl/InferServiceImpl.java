package top.aias.training.service.impl;

import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.translate.TranslateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.aias.training.infer.FeatureExtraction;
import top.aias.training.infer.ImageClassification;
import top.aias.training.service.InferService;
import top.aias.training.training.models.ClassPrediction;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 推理服务
 *
 * @author Calvin
 * @mail 179209347@qq.com
 * @website www.aias.top
 */
@Service
public class InferServiceImpl implements InferService {

    private Logger logger = LoggerFactory.getLogger(InferServiceImpl.class);
    
	public String getClassificationInfo(String newModelPath, InputStream inputStream, List<String> labels) {
		try {
			Image img = ImageFactory.getInstance().fromInputStream(inputStream);
			ClassPrediction classifications = ImageClassification.predict(newModelPath, img, labels);
			return classifications.toString();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public String getClassificationInfoForUrl(String newModelPath, String imageUrl, List<String> labels) {
		try {
			Image img = ImageFactory.getInstance().fromUrl(imageUrl);
			ClassPrediction classifications = ImageClassification.predict(newModelPath, img, labels);
			return classifications.toString();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public float[] feature(String newModelPath,Image image) {
		try {
			float[] feature = FeatureExtraction.predict(newModelPath,image);
			return feature;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public String compare(String newModelPath,Image img1, Image img2) throws IOException {
		float[] feature1 = FeatureExtraction.predict(newModelPath,img1);
		float[] feature2 = FeatureExtraction.predict(newModelPath,img2);
		return Float.toString(calculSimilar(feature1, feature2));
	}

	public float calculSimilar(float[] feature1, float[] feature2) {
		float ret = 0.0f;
		float mod1 = 0.0f;
		float mod2 = 0.0f;
		int length = feature1.length;
		for (int i = 0; i < length; ++i) {
			ret += feature1[i] * feature2[i];
			mod1 += feature1[i] * feature1[i];
			mod2 += feature2[i] * feature2[i];
		}
		return (float) ((ret / Math.sqrt(mod1) / Math.sqrt(mod2) + 1) / 2.0f);
	}
}
