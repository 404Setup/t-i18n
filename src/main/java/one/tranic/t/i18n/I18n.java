package one.tranic.t.i18n;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public enum I18n {
    SnakeYAML {
        private final ResourceLoader resourceLoader = new ResourceLoader() {
            @Override
            protected String getFileExtension() {
                return ".yml";
            }

            @Override
            protected String getAlternativeFileExtension() {
                return ".yaml";
            }

            @Override
            protected Map<String, String> parseInputStream(InputStream inputStream) throws IOException {
                org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
                Map<String, Object> yamlMap = yaml.load(inputStream);
                Map<String, String> result = new HashMap<>();
                flattenYaml(yamlMap, "", result);
                return result;
            }

            @Override
            protected String getFormatName() {
                return "YAML";
            }
        };

        @Override
        @NotNull
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) throws IOException {
            return resourceLoader.load(clazz, namespace, locale);
        }
    },
    GSON {
        private final com.google.gson.Gson gson = new com.google.gson.Gson();

        private final ResourceLoader resourceLoader = new ResourceLoader() {
            @Override
            protected String getFileExtension() {
                return ".json";
            }

            @Override
            protected Map<String, String> parseInputStream(InputStream inputStream) throws IOException {
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                com.google.gson.reflect.TypeToken<Map<String, String>> typeToken =
                        new com.google.gson.reflect.TypeToken<>() {
                        };
                Map<String, String> jsonMap = gson.fromJson(reader, typeToken.getType());
                Map<String, String> result = new HashMap<>();
                if (jsonMap != null) result.putAll(jsonMap);
                return result;
            }

            @Override
            protected String getFormatName() {
                return "JSON";
            }
        };

        @Override
        @NotNull
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) throws IOException {
            return resourceLoader.load(clazz, namespace, locale);
        }
    },
    PROPERTIES {
        private final ResourceLoader resourceLoader = new ResourceLoader() {
            @Override
            protected String getFileExtension() {
                return ".properties";
            }

            @Override
            protected Map<String, String> parseInputStream(InputStream inputStream) throws IOException {
                Properties properties = new Properties();
                properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                Map<String, String> result = new HashMap<>();
                for (String key : properties.stringPropertyNames()) {
                    result.put(key, properties.getProperty(key));
                }
                return result;
            }

            @Override
            protected String getFormatName() {
                return "Properties";
            }
        };

        @Override
        @NotNull
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) throws IOException {
            return resourceLoader.load(clazz, namespace, locale);
        }
    };

    @SuppressWarnings("unchecked")
    private static void flattenYaml(Map<String, Object> yamlMap, String prefix, Map<String, String> result) {
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

    private static @NotNull String getBasePath(@Nullable String namespace, @NotNull Locale locale) {
        return namespace != null ? namespace + "/" + locale : locale.toString();
    }

    private static InputStream getResource(Class<?> clazz, @NotNull String filename) {
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

    abstract @NotNull Map<String, String> load(Class<?> clazz, String namespace, Locale locale) throws IOException;

    private abstract static class ResourceLoader {
        protected abstract String getFileExtension();

        protected String getAlternativeFileExtension() {
            return null;
        }

        protected abstract String getFormatName();

        protected abstract Map<String, String> parseInputStream(InputStream inputStream) throws IOException;

        public Map<String, String> load(Class<?> clazz, String namespace, Locale locale) throws IOException {
            String basePath = getBasePath(namespace, locale);

            InputStream inputStream = getResource(clazz, basePath + getFileExtension());

            String alternativeExt = getAlternativeFileExtension();
            if (inputStream == null && alternativeExt != null) {
                inputStream = getResource(clazz, basePath + alternativeExt);
            }

            if (inputStream == null && !locale.equals(Locale.ENGLISH)) {
                return load(clazz, namespace, Locale.ENGLISH);
            }

            if (inputStream == null) {
                throw new IOException("Failed to load " + getFormatName() + " file for "
                        + clazz.getName() + " in " + locale);
            }

            try (InputStream is = inputStream) {
                return parseInputStream(is);
            }
        }
    }
}