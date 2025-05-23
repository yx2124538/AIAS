package top.aias.training.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import top.aias.training.domain.TrainArgument;
import top.aias.training.service.TrainArgumentService;

import java.io.*;

/**
 * 超参数服务
 *
 * @author Calvin
 * @mail 179209347@qq.com
 * @website www.aias.top
 */
@Service
public class TrainArgumentServiceImpl implements TrainArgumentService {
    private static final String TRAIN_CONFIG_FILE = "train-argument.json";

    private Logger logger = LoggerFactory.getLogger(TrainArgumentServiceImpl.class);
    private TrainArgument trainArgument;

    public TrainArgumentServiceImpl() {
        StringBuilder sb = new StringBuilder();
        try {
            String path = System.getProperty("user.dir");
            File file = new File(path, TRAIN_CONFIG_FILE);
            BufferedReader br;
            if (file.exists()) {
                br = new BufferedReader(new FileReader(file));
            } else {
                InputStreamReader inputStreamReader = new InputStreamReader(new ClassPathResource(TRAIN_CONFIG_FILE).getInputStream());
                br = new BufferedReader(inputStreamReader);
            }

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            logger.error("Storage file read error", e);
        }
        String jsonStr = sb.toString();
        if (!StringUtils.isBlank(jsonStr)) {
            trainArgument = new Gson().fromJson(jsonStr, new TypeToken<TrainArgument>() {
            }.getType());
        } else {
            trainArgument = new TrainArgument();
            trainArgument.setBatchSize(32);
            trainArgument.setEpoch(1);
        }
    }

    /**
     * 保存设置
     * save config
     */
    public void saveTrainArgument() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gson.toJson(trainArgument);
        try {
            File file = new File(TRAIN_CONFIG_FILE);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.print(jsonStr);
        } catch (FileNotFoundException e) {
            logger.error("Training argument file not found", e);
        }
    }

    /**
     * 编辑
     * edit
     *
     * @param trainArgument
     */
    public void update(TrainArgument trainArgument) {
        this.trainArgument = trainArgument;
        saveTrainArgument();
    }


    public TrainArgument getTrainArgument() {
        return trainArgument;
    }
}