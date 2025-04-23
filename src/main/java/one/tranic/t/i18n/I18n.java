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

// TODO: This is for evaluation purposes only and has not been tested.
public enum I18n {
    BukkitYAML {
        @Override
        @NotNull
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) throws IOException {
            Map<String, String> result = new HashMap<>();

            try (InputStream inputStream = getYAMLStream(clazz, namespace, locale)) {
                if (inputStream != null) {
                    org.bukkit.configuration.file.YamlConfiguration yaml = new org.bukkit.configuration.file.YamlConfiguration();
                    try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                        yaml.load(reader);
                        Map<String, Object> yamlMap = yaml.getValues(false);
                        flattenYaml(yamlMap, "", result);
                    } catch (Exception e) {
                        throw new IOException("Failed to load YAML file for " + clazz.getName() + " in " + locale.getLanguage(), e);
                    }
                } else {
                    throw new IOException("Failed to load YAML file for " + clazz.getName() + " in " + locale.getLanguage());
                }
            }

            return result;
        }
    },
    SimpleYAML {
        @Override
        @NotNull
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) throws IOException {
            Map<String, String> result = new HashMap<>();

            try (InputStream inputStream = getYAMLStream(clazz, namespace, locale)) {
                if (inputStream != null) {
                    org.simpleyaml.configuration.file.YamlConfiguration yaml = new org.simpleyaml.configuration.file.YamlConfiguration();
                    yaml.load(inputStream);
                    Map<String, Object> yamlMap = yaml.getMapValues(false);
                    flattenYaml(yamlMap, "", result);
                } else {
                    throw new IOException("Failed to load YAML file for " + clazz.getName() + " in " + locale.getLanguage());
                }
            }

            return result;
        }
    },
    SnakeYAML {
        @Override
        @NotNull
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) throws IOException {
            Map<String, String> result = new HashMap<>();

            try (InputStream inputStream = getYAMLStream(clazz, namespace, locale)) {
                if (inputStream != null) {
                    org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
                    Map<String, Object> yamlMap = yaml.load(inputStream);
                    flattenYaml(yamlMap, "", result);
                } else {
                    throw new IOException("Failed to load YAML file for " + clazz.getName() + " in " + locale.getLanguage());
                }
            }

            return result;
        }
    },
    GSON {
        private final com.google.gson.Gson gson = new com.google.gson.Gson();

        @Override
        @NotNull
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) throws IOException {
            Map<String, String> result = new HashMap<>();

            try (InputStream inputStream = getJsonStream(clazz, namespace, locale)) {
                if (inputStream != null) {
                    InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

                    com.google.gson.reflect.TypeToken<Map<String, String>> typeToken = new com.google.gson.reflect.TypeToken<>() {
                    };
                    Map<String, String> jsonMap = gson.fromJson(reader, typeToken.getType());

                    if (jsonMap != null) result.putAll(jsonMap);
                } else {
                    throw new IOException("Failed to load JSON file for " + clazz.getName() + " in " + locale.getLanguage());
                }
            }

            return result;
        }
    };

    @SuppressWarnings("unchecked")
    private static void flattenYaml(Map<String, Object> yamlMap, String prefix, Map<String, String> result) {
        for (Map.Entry<String, Object> entry : yamlMap.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                flattenYaml((Map<String, Object>) value, key, result);
            } else {
                if (value != null) result.put(key, value.toString());
            }
        }
    }

    private static @NotNull String getBasePath(@Nullable String namespace, @NotNull Locale locale) {
        return namespace != null ? namespace + "/" + locale.getLanguage() : locale.getLanguage();
    }

    private static @Nullable InputStream getYAMLStream(Class<?> clazz, @Nullable String namespace, @NotNull Locale locale) {
        var basePath = getBasePath(namespace, locale);

        InputStream inputStream = getResource(clazz, basePath + ".yml");
        if (inputStream == null) inputStream = clazz.getResourceAsStream(basePath + ".yaml");

        if (inputStream == null && !locale.equals(Locale.ENGLISH))
            return getYAMLStream(clazz, namespace, Locale.ENGLISH);
        return inputStream;
    }

    private static @Nullable InputStream getJsonStream(Class<?> clazz, @Nullable String namespace, @NotNull Locale locale) {
        var basePath = getBasePath(namespace, locale);

        var inputStream = getResource(clazz, basePath + ".json");
        if (inputStream == null && !locale.equals(Locale.ENGLISH))
            return getJsonStream(clazz, namespace, Locale.ENGLISH);
        return inputStream;
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
}