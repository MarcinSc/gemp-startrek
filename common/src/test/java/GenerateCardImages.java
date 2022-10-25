import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GenerateCardImages {
    private static File sourceImageFolder = new File("D:\\dev\\startrek2e\\sets\\setimages\\general");
    private static String outputFolder = "D:\\dev\\gemp-startrek\\internal\\image\\image";
    private static String fileName = "ST2E-EN[set][number].jpg";

    public static void main(String[] args) {
        int setNo = 1;
        int cardCount = 415;

        NumberFormat setNumberFormat = new DecimalFormat("00");
        NumberFormat cardNumberFormat = new DecimalFormat("000");

        File setFolder = new File(outputFolder, "set" + setNo);
        setFolder.mkdirs();

        try {
            for (int i = 1; i <= cardCount; i++) {
                String name = fileName;
                name = name.replace("[set]", setNumberFormat.format(setNo));
                name = name.replace("[number]", cardNumberFormat.format(i));
                File cardImage = new File(sourceImageFolder, name);
                File outputImage = new File(setFolder, i + ".png");

                BufferedImage fullImage = ImageIO.read(cardImage);
                BufferedImage resultImage = new BufferedImage(308, 199, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = resultImage.createGraphics();
                try {
                    graphics.drawImage(fullImage, 0, 0, 308, 199, 17, 71, 325, 270, null);
                } finally {
                    graphics.dispose();
                }
                ImageIO.write(resultImage, "png", outputImage);
            }
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }
}
