package one.tranic.t.i18n.loader;

import one.tranic.t.i18n.BaseLoader;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Map;

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
public class YmlLoader implements I18n {
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
        protected Map<String, String> parseInputStream(@NotNull InputStream inputStream) {
            org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
            Map<String, Object> yamlMap = yaml.load(inputStream);
            Map<String, String> result = BaseLoader.createMap();
            I18n.flattenYaml(yamlMap, "", result);
            return result;
        }

        @Override
        protected String getFormatName() {
            return "YAML";
        }
    };

    @Override
    public @NotNull ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
