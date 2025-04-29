package one.tranic.t.i18n;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public enum I18n {
    /**
     * Example Yaml:
     * <pre>{@code
     * goldpiglin.test1: Goldpiglin test
     * goldpiglin.boost: boost test
     * goldpiglin
     *      boost:
     *          xyz: Goldpiglin xyzzz
     * }</pre>
     */
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
            protected Map<String, String> parseInputStream(@NotNull InputStream inputStream) throws IOException {
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
        Map<String, String> load(File file, Class<?> clazz, String namespace, Locale locale) throws IOException {
            return resourceLoader.load(file, clazz, namespace, locale);
        }
    },
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
    GSON {
        private final com.google.gson.Gson gson = new com.google.gson.Gson();

        private final ResourceLoader resourceLoader = new ResourceLoader() {
            @Override
            protected String getFileExtension() {
                return ".json";
            }

            @Override
            protected Map<String, String> parseInputStream(@NotNull InputStream inputStream) throws IOException {
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
        Map<String, String> load(File file, Class<?> clazz, String namespace, Locale locale) throws IOException {
            return resourceLoader.load(file, clazz, namespace, locale);
        }
    },
    /**
     * Example Properties:
     * <pre>{@code
     * goldpiglin.test1 = Goldpiglin test
     * goldpiglin.boost = boost test
     * goldpiglin.boost.xyz = Goldpiglin xyzzz
     * }</pre>
     */
    PROPERTIES {
        private final ResourceLoader resourceLoader = new ResourceLoader() {
            @Override
            protected String getFileExtension() {
                return ".properties";
            }

            @Override
            protected Map<String, String> parseInputStream(@NotNull InputStream inputStream) throws IOException {
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
        Map<String, String> load(File file, Class<?> clazz, String namespace, Locale locale) throws IOException {
            return resourceLoader.load(file, clazz, namespace, locale);
        }
    },
    /**
     * Example xml:
     * <pre>{@code
     * <messages>
     *      <!-- Using ID attribute method -->
     *      <message id="goldpiglin.test1">Goldpiglin test</message>
     *
     *      <!-- Using node hierarchy method -->
     *      <goldpiglin>
     *          <boost>boost test</boost>
     *          <boost>
     *              <xyz>Goldpiglin xyzzz</xyz>
     *          </boost>
     *      </goldpiglin>
     * </messages>
     * }</pre>
     */
    XML {
        private final ResourceLoader resourceLoader = new ResourceLoader() {
            private final String[] ignoredTags = new String[]{"messages", "resources", "strings", "message"};

            @Override
            protected String getFileExtension() {
                return ".xml";
            }

            @Override
            protected Map<String, String> parseInputStream(@NotNull InputStream inputStream) throws IOException {
                Map<String, String> result = new HashMap<>();
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(inputStream);

                    Element root = document.getDocumentElement();

                    NodeList children = root.getChildNodes();
                    for (int i = 0; i < children.getLength(); i++) {
                        if (children.item(i) instanceof Element child)
                            processXmlElement(child, "", result);
                    }

                    return result;
                } catch (Exception e) {
                    throw new IOException("Failed to parse XML file: " + e.getMessage(), e);
                }
            }

            private void processXmlElement(Element element, String prefix, Map<String, String> result) {
                if (element.hasAttribute("id") && element.getTextContent() != null && !element.getTextContent().trim().isEmpty()) {
                    String key = element.getAttribute("id");
                    result.put(key, element.getTextContent().trim());
                    return;
                }

                String nodeName = element.getNodeName();

                final String newPrefix = getNewPrefix(prefix, nodeName);

                NodeList childElements = element.getElementsByTagName("*");
                boolean hasElementChildren = false;
                for (int i = 0; i < childElements.getLength(); i++) {
                    if (childElements.item(i).getParentNode() == element) {
                        hasElementChildren = true;
                        break;
                    }
                }

                if (!hasElementChildren && element.getTextContent() != null && !element.getTextContent().trim().isEmpty()) {
                    if (!nodeName.equals("messages") && !nodeName.equals("resources") && !nodeName.equals("strings")) {
                        result.put(newPrefix, element.getTextContent().trim());
                    }
                }

                NodeList children = element.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    if (children.item(i) instanceof Element) {
                        processXmlElement((Element) children.item(i), newPrefix, result);
                    }
                }
            }

            private String getNewPrefix(String prefix, String nodeName) {
                boolean isSpecialTag = false;
                for (int i = 0; i < ignoredTags.length; i++) {
                    if (ignoredTags[i].equals(nodeName)) {
                        isSpecialTag = true;
                        break;
                    }
                }

                String newPrefix;
                if (isSpecialTag) {
                    newPrefix = prefix;
                } else {
                    newPrefix = prefix.isEmpty() ? nodeName : prefix + "." + nodeName;
                }
                return newPrefix;
            }

            @Override
            protected String getFormatName() {
                return "XML";
            }
        };

        @Override
        @NotNull
        Map<String, String> load(@Nullable File file, @Nullable Class<?> clazz, @NotNull String namespace, @Nullable Locale locale) throws IOException {
            return resourceLoader.load(file, clazz, namespace, locale);
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

    abstract @NotNull Map<String, String> load(File file, Class<?> clazz, String namespace, Locale locale) throws IOException;

    private abstract static class ResourceLoader {
        protected abstract String getFileExtension();

        protected String getAlternativeFileExtension() {
            return null;
        }

        protected abstract String getFormatName();

        protected abstract Map<String, String> parseInputStream(@NotNull InputStream inputStream) throws IOException;

        public Map<String, String> load(@Nullable File file, @Nullable Class<?> clazz, @NotNull String namespace, @Nullable Locale locale) throws IOException {
            if (file != null) {
                try (InputStream is = new FileInputStream(file)) {
                    return parseInputStream(is);
                } catch (IOException e) {
                    throw new IOException("Failed to load " + getFormatName() + " file for "
                            + file + " in " + locale, e);
                }
            }

            if (clazz == null)
                throw new IOException("Failed to load " + getFormatName() + " file for "
                        + namespace + " in " + locale + ": class is null");
            if (locale == null) locale = Locale.getDefault();

            String basePath = getBasePath(namespace, locale);

            InputStream inputStream = getResource(clazz, basePath + getFileExtension());

            String alternativeExt = getAlternativeFileExtension();
            if (inputStream == null && alternativeExt != null) {
                inputStream = getResource(clazz, basePath + alternativeExt);
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