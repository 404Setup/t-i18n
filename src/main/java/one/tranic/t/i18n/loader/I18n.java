package one.tranic.t.i18n.loader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public interface I18n {
    @SuppressWarnings("unchecked")
    static void flattenYaml(Map<String, Object> yamlMap, String prefix, Map<String, String> result) {
        if (yamlMap == null) return;

        for (Map.Entry<String, Object> entry : yamlMap.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                flattenYaml((Map<String, Object>) value, key, result);
            } else if (value != null) {
                result.put(key, value.toString());
            }
        }
    }

    static @NotNull String getBasePath(@Nullable String namespace, @NotNull Locale locale) {
        return namespace != null ? namespace + "/" + locale : locale.toString();
    }

    static InputStream getResource(Class<?> clazz, @NotNull String filename) {
        try {
            URL url = clazz.getClassLoader().getResource(filename);
            if (url == null) return null;

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    @NotNull ResourceLoader getResourceLoader();

    @NotNull
    default Map<String, String> load(@NotNull InputStream inputStream) throws IOException {
        return getResourceLoader().load(inputStream);
    }

    @NotNull
    default Map<String, String> load(@NotNull File file) throws IOException {
        return getResourceLoader().load(file);
    }

    @NotNull
    default Map<String, String> load(@NotNull Path path, @NotNull Locale locale) throws IOException, IllegalArgumentException {
        return getResourceLoader().load(path, locale);
    }

    @NotNull
    default Map<String, String> load(@NotNull Class<?> clazz, @NotNull String namespace, @NotNull Locale locale) throws IOException, IllegalArgumentException {
        return getResourceLoader().load(clazz, namespace, locale);
    }
}
