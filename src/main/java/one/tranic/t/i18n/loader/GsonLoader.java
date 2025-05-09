package one.tranic.t.i18n.loader;

import one.tranic.t.i18n.BaseLoader;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Example Json:
 * <pre>{@code
 * {
 *     "goldpiglin.test1": "Goldpiglin test",
 *     "goldpiglin.boost": "boost test",
 *     "goldpiglin.boost.xyz": "Goldpiglin xyzzz"
 * }
 * }</pre>
 */
public class GsonLoader implements I18n {
    private final ResourceLoader resourceLoader = new ResourceLoader() {
        private final com.google.gson.Gson gson = new com.google.gson.Gson();

        @Override
        protected String getFileExtension() {
            return ".json";
        }

        @Override
        protected Map<String, String> parseInputStream(@NotNull InputStream inputStream) {
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            com.google.gson.reflect.TypeToken<Map<String, String>> typeToken =
                    new com.google.gson.reflect.TypeToken<>() {
                    };
            Map<String, String> jsonMap = gson.fromJson(reader, typeToken.getType());
            Map<String, String> result = BaseLoader.createMap();
            if (jsonMap != null) result.putAll(jsonMap);
            return result;
        }

        @Override
        protected String getFormatName() {
            return "JSON";
        }
    };

    @Override
    public @NotNull ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
