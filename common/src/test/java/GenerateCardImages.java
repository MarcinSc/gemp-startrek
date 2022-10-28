import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.DummyApplication;
import com.gempukku.startrek.card.CardData;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GenerateCardImages {
    private static File sourceImageFolder = new File("D:\\dev\\startrek2e\\sets\\setimages\\general");
    private static String outputFolder = "D:\\dev\\gemp-startrek\\cardImages";
    private static String fileName = "ST2E-EN[set][number].jpg";
    private static Rectangle nonMissionCardRectangle = new Rectangle(17, 71, 308, 199);
    private static Rectangle missionCardRectangle = new Rectangle(17, 71, 321, 199);

    public static void main(String[] args) {
        Gdx.files = new HeadlessFiles();
        Gdx.app = new DummyApplication();

        CardData cardData = new CardData();
        cardData.initializeCards();

        NumberFormat setNumberFormat = new DecimalFormat("00");
        NumberFormat cardNumberFormat = new DecimalFormat("000");

        try {
            for (ObjectMap.Entry<String, CardDefinition> cardDefinition : cardData.getCardDefinitions()) {
                String cardId = cardDefinition.key;
                CardDefinition cardDef = cardDefinition.value;
                String[] cardIdSplit = cardId.split("_");
                int cardSet = Integer.parseInt(cardIdSplit[0]);
                int cardNumber = Integer.parseInt(cardIdSplit[1]);
                if (shouldSaveCard(cardSet, cardNumber)) {
                    File setFolder = new File(outputFolder, "set" + cardSet);
                    setFolder.mkdirs();

                    String name = fileName;
                    name = name.replace("[set]", setNumberFormat.format(cardSet));
                    name = name.replace("[number]", cardNumberFormat.format(cardNumber));
                    File cardImage = new File(sourceImageFolder, name);
                    File outputImage = new File(setFolder, cardNumber + ".png");

                    Rectangle cardRectangle = getCardRectangle(cardDef);

                    BufferedImage fullImage = ImageIO.read(cardImage);
                    BufferedImage resultImage = new BufferedImage(cardRectangle.width, cardRectangle.height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = resultImage.createGraphics();
                    try {
                        graphics.drawImage(fullImage, 0, 0, cardRectangle.width, cardRectangle.height,
                                cardRectangle.x, cardRectangle.y, cardRectangle.x + cardRectangle.width, cardRectangle.y + cardRectangle.height,
                                null);
                    } finally {
                        graphics.dispose();
                    }
                    ImageIO.write(resultImage, "png", outputImage);
                }
            }
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    private static Rectangle getCardRectangle(CardDefinition cardDef) {
        if (cardDef.getType() == CardType.Mission)
            return missionCardRectangle;
        return nonMissionCardRectangle;
    }

    private static boolean shouldSaveCard(int cardSet, int cardNumber) {
        return cardSet == 1;
    }
}
