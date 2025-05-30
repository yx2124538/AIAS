package top.aias.sd.utils;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.ndarray.NDArray;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
/**
 * 图像工具类
 *
 * @author Calvin
 * @mail 179209347@qq.com
 * @website www.aias.top
 */
public class ImageUtils {

    /**
     * 保存BufferedImage图片
     *
     * @param bufferedImage
     * @param name
     * @param path
     */
    public static void saveImage(BufferedImage bufferedImage, String name, String path) {
        try {
            Path outputDir = Paths.get(path);
            Path imagePath = outputDir.resolve(name);
            File output = imagePath.toFile();
            ImageIO.write(bufferedImage, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存DJL图片
     *
     * @param img
     * @param name
     * @param path
     */
    public static void saveImage(Image img, String name, String path) {
        Path outputDir = Paths.get(path);
        Path imagePath = outputDir.resolve(name);
        // OpenJDK 不能保存 jpg 图片的 alpha channel
        try {
            img.save(Files.newOutputStream(imagePath), "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存图片,含检测框
     *
     * @param img
     * @param detection
     * @param name
     * @param path
     * @throws IOException
     */
    public static void saveBoundingBoxImage(
            Image img, DetectedObjects detection, String name, String path) throws IOException {
        // Make image copy with alpha channel because original image was jpg
        img.drawBoundingBoxes(detection);
        Path outputDir = Paths.get(path);
        Files.createDirectories(outputDir);
        Path imagePath = outputDir.resolve(name);
        // OpenJDK can't save jpg with alpha channel
        img.save(Files.newOutputStream(imagePath), "png");
    }

    /**
     * 画矩形
     *
     * @param mat
     * @param box
     * @return
     */
    public static void drawRect(Mat mat, NDArray box) {

        float[] points = box.toFloatArray();
        List<Point> list = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Point point = new Point((int) points[2 * i], (int) points[2 * i + 1]);
            list.add(point);
        }

        Imgproc.line(mat, list.get(0), list.get(1), new Scalar(0, 255, 0), 1);
        Imgproc.line(mat, list.get(1), list.get(2), new Scalar(0, 255, 0), 1);
        Imgproc.line(mat, list.get(2), list.get(3), new Scalar(0, 255, 0), 1);
        Imgproc.line(mat, list.get(3), list.get(0), new Scalar(0, 255, 0), 1);
    }

    /**
     * 画矩形
     *
     * @param mat
     * @param box
     * @return
     */
    public static void drawRectWithText(Mat mat, NDArray box, String text) {

        float[] points = box.toFloatArray();
        List<Point> list = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Point point = new Point((int) points[2 * i], (int) points[2 * i + 1]);
            list.add(point);
        }

        Imgproc.line(mat, list.get(0), list.get(1), new Scalar(0, 255, 0), 1);
        Imgproc.line(mat, list.get(1), list.get(2), new Scalar(0, 255, 0), 1);
        Imgproc.line(mat, list.get(2), list.get(3), new Scalar(0, 255, 0), 1);
        Imgproc.line(mat, list.get(3), list.get(0), new Scalar(0, 255, 0), 1);
        // 中文乱码
        Imgproc.putText(mat, text, list.get(0), Imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX, 1.0, new Scalar(0, 255, 0), 1);
    }

    /**
     * 画检测框(有倾斜角)
     *
     * @param image
     * @param box
     */
    public static void drawImageRect(BufferedImage image, NDArray box) {
        float[] points = box.toFloatArray();
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];

        for (int i = 0; i < 4; i++) {
            xPoints[i] = (int) points[2 * i];
            yPoints[i] = (int) points[2 * i + 1];
        }
        xPoints[4] = xPoints[0];
        yPoints[4] = yPoints[0];

        // 将绘制图像转换为Graphics2D
        Graphics2D g = (Graphics2D) image.getGraphics();
        try {
            g.setColor(new Color(0, 255, 0));
            // 声明画笔属性 ：粗 细（单位像素）末端无修饰 折线处呈尖角
            BasicStroke bStroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g.setStroke(bStroke);
            g.drawPolyline(xPoints, yPoints, 5); // xPoints, yPoints, nPoints
        } finally {
            g.dispose();
        }
    }

    /**
     * 画检测框(有倾斜角)和文本
     *
     * @param image
     * @param box
     * @param text
     */
    public static void drawImageRectWithText(BufferedImage image, NDArray box, String text) {
        float[] points = box.toFloatArray();
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];

        for (int i = 0; i < 4; i++) {
            xPoints[i] = (int) points[2 * i];
            yPoints[i] = (int) points[2 * i + 1];
        }
        xPoints[4] = xPoints[0];
        yPoints[4] = yPoints[0];

        // 将绘制图像转换为Graphics2D
        Graphics2D g = (Graphics2D) image.getGraphics();
        try {
            int fontSize = 32;
            Font font = new Font("楷体", Font.PLAIN, fontSize);
            g.setFont(font);
            g.setColor(new Color(0, 0, 255));
            // 声明画笔属性 ：粗 细（单位像素）末端无修饰 折线处呈尖角
            BasicStroke bStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g.setStroke(bStroke);
            g.drawPolyline(xPoints, yPoints, 5); // xPoints, yPoints, nPoints
            g.drawString(text, xPoints[0], yPoints[0]);
        } finally {
            g.dispose();
        }
    }

    /**
     * 画检测框
     *
     * @param image
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void drawImageRect(BufferedImage image, int x, int y, int width, int height) {
        // 将绘制图像转换为Graphics2D
        Graphics2D g = (Graphics2D) image.getGraphics();
        try {
            g.setColor(new Color(0, 255, 0));
            // 声明画笔属性 ：粗 细（单位像素）末端无修饰 折线处呈尖角
            BasicStroke bStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            g.setStroke(bStroke);
            g.drawRect(x, y, width, height);
        } finally {
            g.dispose();
        }
    }

    /**
     * 显示文字
     *
     * @param image
     * @param text
     * @param x
     * @param y
     */
    public static void drawImageText(BufferedImage image, String text, int x, int y) {
        Graphics graphics = image.getGraphics();
        int fontSize = 32;
        Font font = new Font("楷体", Font.PLAIN, fontSize);
        try {
            graphics.setFont(font);
            graphics.setColor(new Color(0, 0, 255));
            int strWidth = graphics.getFontMetrics().stringWidth(text);
            graphics.drawString(text, x, y);
        } finally {
            graphics.dispose();
        }
    }

    /**
     * BufferedImage 转 base64
     * @param image
     * @param type - JPG、PNG、GIF、BMP 等
     * @return
     * @throws IOException
     */
    public static String toBase64(BufferedImage image, String type) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, type, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static BufferedImage removeBg(BufferedImage image) {
        // 获取图片的宽度和高度
        int width = image.getWidth();
        int height = image.getHeight();

        // 创建一个新的BufferedImage，类型为ARGB，用于存储转换后的图片
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // 遍历每个像素
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 获取当前像素的RGB值
                int rgba = image.getRGB(x, y);
                // 将RGB值转换为颜色对象
                Color color = new Color(rgba, true);
                // 如果当前像素是黑色，则将Alpha通道值设置为0
                if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                    color = new Color(0, 0, 0, 0);
                }

                // 将修改后的颜色设置到新的BufferedImage中
                newImage.setRGB(x, y, color.getRGB());
            }
        }
        return newImage;
    }
}