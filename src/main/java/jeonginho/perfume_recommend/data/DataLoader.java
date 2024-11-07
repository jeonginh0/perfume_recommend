package jeonginho.perfume_recommend.data;

import com.nimbusds.jose.shaded.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    public static List<String> loadPerfumeNames(String filePath) throws IOException {
        List<String> perfumeNames = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath);
             JsonReader jsonReader = new JsonReader(reader)) {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                jsonReader.beginObject();
                // Assuming perfume name is stored under the key "perfume"
                while (jsonReader.hasNext()) {
                    String name = jsonReader.nextName();
                    if (name.equals("perfume")) {
                        perfumeNames.add(jsonReader.nextString());
                    } else {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
            }
            jsonReader.endArray();
        }
        return perfumeNames;
    }
}
