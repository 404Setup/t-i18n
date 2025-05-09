package one.tranic.t.i18n.loader;

import one.tranic.t.i18n.BaseLoader;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public enum BaseI18n implements I18n {
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
                Map<String, String> result = BaseLoader.createMap();
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
        public @NotNull ResourceLoader getResourceLoader() {
            return resourceLoader;
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
                Map<String, String> result = BaseLoader.createMap();
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
        public @NotNull ResourceLoader getResourceLoader() {
            return resourceLoader;
        }
    };
}