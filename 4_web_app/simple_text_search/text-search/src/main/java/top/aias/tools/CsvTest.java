package top.aias.tools;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * csv 测试工具
 *
 * @author Calvin
 * @mail 179209347@qq.com
 * @website www.aias.top
 **/
public class CsvTest {

    public static void main(String[] args) throws InterruptedException {

        File file = new File("src/test/resources/example.csv");
        CsvReader csvReader = new CsvReader();

        try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
            CsvRow row;
            while ((row = csvParser.nextRow()) != null) {
                System.out.println("First column: " + row.getField(0));
                System.out.println("Second column: " + row.getField(1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
