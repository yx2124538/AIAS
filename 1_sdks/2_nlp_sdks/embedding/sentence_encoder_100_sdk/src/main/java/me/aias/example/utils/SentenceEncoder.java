package me.aias.example.utils;

import ai.djl.Device;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 *
 * @author Calvin
 *
 * @email 179209347@qq.com
 **/

public final class SentenceEncoder {

  private static final Logger logger = LoggerFactory.getLogger(SentenceEncoder.class);

  public SentenceEncoder() {}

  public Criteria<String, float[]> criteria() throws IOException {

    Path modelFile = Paths.get("src/main/resources/sentencepiece.bpe.model");
    SpTokenizer tokenizer = new SpTokenizer(modelFile);
    SpProcessor processor = tokenizer.getProcessor();

    Criteria<String, float[]> criteria =
        Criteria.builder()
            .setTypes(String.class, float[].class)
            .optModelPath(Paths.get("models/paraphrase-xlm-r-multilingual-v1.pt"))
            .optTranslator(new M100Translator(processor))
            .optEngine("PyTorch") // Use PyTorch engine
            .optDevice(Device.gpu())
            .optProgress(new ProgressBar())
            .build();

    return criteria;
  }
}
