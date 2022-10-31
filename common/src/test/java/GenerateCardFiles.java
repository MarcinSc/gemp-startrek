import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.gempukku.libgdx.template.JsonUtils;
import org.mockito.internal.util.io.IOUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class GenerateCardFiles {
    public static void main(String[] args) {
        File cardFolder = new File("D:\\dev\\gemp-startrek\\cards\\card");
        File[] setFolders = cardFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        Array<JsonValue> setLines = new Array<>();
        for (File setFolder : setFolders) {
            String setName = setFolder.getName();

            processSetFolder(setFolder);

            JsonValue setJson = new JsonValue(JsonValue.ValueType.object);
            setJson.addChild("tpl:extends", new JsonValue("card/" + setName + "/" + setName + ".json"));
            setLines.add(setJson);
        }
        JsonValue cardsJson = new JsonValue(JsonValue.ValueType.object);
        JsonValue setsArray = JsonUtils.convertToJsonArray(setLines,
                new JsonUtils.JsonConverter<JsonValue>() {
                    @Override
                    public JsonValue convert(JsonValue value) {
                        return value;
                    }
                });
        cardsJson.addChild("sets", setsArray);
        IOUtil.writeText(cardsJson.toJson(JsonWriter.OutputType.json), new File(cardFolder, "cards.json"));
    }

    private static void processSetFolder(File setFolder) {
        String[] cardFiles = setFolder.list(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return !name.startsWith("set") && name.endsWith(".json");
                    }
                });
        JsonValue cardsJson = new JsonValue(JsonValue.ValueType.object);
        for (String cardFile : cardFiles) {
            String cardId = cardFile.substring(0, cardFile.length() - 5);
            JsonValue cardObject = new JsonValue(JsonValue.ValueType.object);
            cardObject.addChild("tpl:extends", new JsonValue("card/" + setFolder.getName() + "/" + cardFile));
            cardsJson.addChild(cardId, cardObject);
        }

        JsonValue result = new JsonValue(JsonValue.ValueType.object);
        result.addChild("cards", cardsJson);
        IOUtil.writeText(result.toJson(JsonWriter.OutputType.json), new File(setFolder, setFolder.getName() + ".json"));
    }
}
