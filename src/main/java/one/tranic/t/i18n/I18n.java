package one.tranic.t.i18n;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// TODO: This is for evaluation purposes only and has not been tested.
public enum I18n {
    BukkitYAML {
        @Override
        @Nullable
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) {
            Map<String, String> result = new HashMap<>();
            String basePath = getBasePath(namespace, locale);

            try (InputStream inputStream = getYAMLStream(clazz, basePath)) {
                if (inputStream != null) {
                    org.bukkit.configuration.file.YamlConfiguration yaml = new org.bukkit.configuration.file.YamlConfiguration();
                    try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                        yaml.load(reader);
                        Map<String, Object> yamlMap = yaml.getValues(false);
                        flattenYaml(yamlMap, "", result);
                    }
                } else {
                    return null;
                }
            } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
                e.printStackTrace();
            }

            return result;
        }
    },
    SimpleYAML {
        @Override
        @Nullable
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) {
            Map<String, String> result = new HashMap<>();
            String basePath = getBasePath(namespace, locale);

            try (InputStream inputStream = getYAMLStream(clazz, basePath)) {
                if (inputStream != null) {
                    org.simpleyaml.configuration.file.YamlConfiguration yaml = new org.simpleyaml.configuration.file.YamlConfiguration();
                    yaml.load(inputStream);
                    Map<String, Object> yamlMap = yaml.getMapValues(false);
                    flattenYaml(yamlMap, "", result);
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    },
    SnakeYAML {
        @Override
        @Nullable
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) {
            Map<String, String> result = new HashMap<>();
            String basePath = getBasePath(namespace, locale);

            try (InputStream inputStream = getYAMLStream(clazz, basePath)) {
                if (inputStream != null) {
                    org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
                    Map<String, Object> yamlMap = yaml.load(inputStream);
                    flattenYaml(yamlMap, "", result);
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    },
    GSON {
        private final com.google.gson.Gson gson = new com.google.gson.Gson();

        @Override
        @Nullable
        Map<String, String> load(Class<?> clazz, String namespace, Locale locale) {
            Map<String, String> result = new HashMap<>();
            String filePath = getBasePath(namespace, locale);

            try (InputStream inputStream = getJsonStream(clazz, filePath)) {
                if (inputStream != null) {
                    InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

                    com.google.gson.reflect.TypeToken<Map<String, String>> typeToken = new com.google.gson.reflect.TypeToken<>() {
                    };
                    Map<String, String> jsonMap = gson.fromJson(reader, typeToken.getType());

                    if (jsonMap != null) {
                        result.putAll(jsonMap);
                    }
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                result.put(key, value != null ? value.toString() : "");
            }
        }
    }


    private static @NotNull String getBasePath(@Nullable String namespace, @NotNull Locale locale) {
        return namespace != null ? namespace + "/" + locale : locale.toString();
    }

    private static @Nullable InputStream getYAMLStream(Class<?> clazz, String basePath) {
        InputStream inputStream = clazz.getResourceAsStream(basePath + ".yml");
        if (inputStream == null) {
            inputStream = clazz.getResourceAsStream(basePath + ".yaml");
        }
        return inputStream;
    }

    private static @Nullable InputStream getJsonStream(Class<?> clazz, String basePath) {
        return clazz.getResourceAsStream(basePath + ".json");
    }


    abstract @Nullable Map<String, String> load(Class<?> clazz, String namespace, Locale locale);
}