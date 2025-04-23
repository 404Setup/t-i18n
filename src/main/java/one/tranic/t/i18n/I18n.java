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

            try (InputStream inputStream = getYAMLStream(clazz, namespace, locale)) {
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

            try (InputStream inputStream = getYAMLStream(clazz, namespace, locale)) {
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

            try (InputStream inputStream = getYAMLStream(clazz, namespace, locale)) {
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

            try (InputStream inputStream = getJsonStream(clazz, namespace, locale)) {
                if (inputStream != null) {
                    InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

                    com.google.gson.reflect.TypeToken<Map<String, String>> typeToken = new com.google.gson.reflect.TypeToken<>() {
                    };
                    Map<String, String> jsonMap = gson.fromJson(reader, typeToken.getType());

                    if (jsonMap != null) result.putAll(jsonMap);
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
                if (value != null) result.put(key, value.toString());
            }
        }
    }

    private static @NotNull String getBasePath(@Nullable String namespace, @NotNull Locale locale) {
        return namespace != null ? namespace + "/" + locale : locale.toString();
    }

    private static @Nullable InputStream getYAMLStream(Class<?> clazz, @Nullable String namespace, @NotNull Locale locale) {
        var basePath = getBasePath(namespace, locale);

        InputStream inputStream = clazz.getResourceAsStream(basePath + ".yml");
        if (inputStream == null) inputStream = clazz.getResourceAsStream(basePath + ".yaml");

        if (inputStream == null && !locale.equals(Locale.ENGLISH))
            return getYAMLStream(clazz, namespace, Locale.ENGLISH);
        return inputStream;
    }

    private static @Nullable InputStream getJsonStream(Class<?> clazz, @Nullable String namespace, @NotNull Locale locale) {
        var basePath = getBasePath(namespace, locale);

        var inputStream = clazz.getResourceAsStream(basePath + ".json");
        if (inputStream == null && !locale.equals(Locale.ENGLISH))
            return getJsonStream(clazz, namespace, Locale.ENGLISH);
        return inputStream;
    }

    abstract @Nullable Map<String, String> load(Class<?> clazz, String namespace, Locale locale);
}