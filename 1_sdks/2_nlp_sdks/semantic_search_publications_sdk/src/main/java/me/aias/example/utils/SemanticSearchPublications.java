package me.aias.example.utils;

import ai.djl.Device;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 *
 * @author Calvin
 *
 * @email 179209347@qq.com
 **/
public final class SemanticSearchPublications {

  private static final Logger logger = LoggerFactory.getLogger(SemanticSearchPublications.class);

  public SemanticSearchPublications() {}

  public Criteria<String[], float[]> criteria() {

    Criteria<String[], float[]> criteria =
        Criteria.builder()
            .setTypes(String[].class, float[].class)
            .optModelPath(Paths.get("models/allenai-specter.zip"))
            .optTranslator(new SemanticSearchTranslator())
            .optEngine("PyTorch") // Use PyTorch engine
            .optDevice(Device.cpu())
            .optProgress(new ProgressBar())
            .build();

    return criteria;
  }
}
