package one.tranic.t.i18n.loader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public abstract class ResourceLoader {
    private static final boolean DEBUG = Boolean.getBoolean("tranic.i18n.debug");

    protected abstract String getFileExtension();

    protected String getAlternativeFileExtension() {
        return null;
    }

    protected abstract String getFormatName();

    protected abstract Map<String, String> parseInputStream(@NotNull InputStream inputStream) throws IOException;

    @SuppressWarnings("ConstantConditions")
    protected Map<String, String> load(@NotNull InputStream inputStream) throws IOException {
        if (inputStream == null)
            throw new IOException("Failed to load " + getFormatName() + " file: input stream is null");

        return parseInputStream(inputStream);
    }

    @SuppressWarnings("ConstantConditions")
    public Map<String, String> load(@NotNull File file) throws IOException {
        if (file == null)
            throw new IOException("Failed to load " + getFormatName() + " file: file is null");
        if (!file.exists())
            throw new IOException("Failed to load " + getFormatName() + " file: file does not exist");
        if (!file.isFile())
            throw new IOException("Failed to load " + getFormatName() + " file: file is not a file");
        if (!file.canRead())
            throw new IOException("Failed to load " + getFormatName() + " file: file is not readable");
        try (InputStream is = new FileInputStream(file)) {
            return parseInputStream(is);
        } catch (IOException e) {
            throw new IOException("Failed to load " + getFormatName() + " file for "
                    + file, e);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public Map<String, String> load(@NotNull Path path, @NotNull Locale locale) throws IOException, IllegalArgumentException {
        if (path == null) throw new IllegalArgumentException("Path must not be null");
        if (locale == null) locale = Locale.getDefault();

        var file = path.resolve(locale + getFileExtension()).toFile();
        if (!file.exists()) {
            String alternativeExt = getAlternativeFileExtension();
            if (alternativeExt != null) {
                file = path.resolve(locale + getAlternativeFileExtension()).toFile();
                if (!file.exists())
                    throw new IOException("Failed to load " + getFormatName() + " file for "
                            + path + " in " + locale);
            }
        }

        try (InputStream is = new FileInputStream(file)) {
            return parseInputStream(is);
        } catch (IOException e) {
            throw new IOException("Failed to load " + getFormatName() + " file for "
                    + file, e);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public Map<String, String> load(@NotNull Class<?> clazz, @NotNull String namespace, @NotNull Locale locale) throws IOException, IllegalArgumentException {
        if (clazz == null)
            throw new IOException("Failed to load " + getFormatName() + " file for "
                    + namespace + " in " + locale + ": class is null");
        if (namespace == null)
            throw new IllegalArgumentException("Namespace must not be null when loading from a class");
        if (locale == null) locale = Locale.getDefault();

        String basePath = I18n.getBasePath(namespace, locale);

        InputStream inputStream = I18n.getResource(clazz, basePath + getFileExtension());

        String alternativeExt = getAlternativeFileExtension();
        if (inputStream == null && alternativeExt != null) {
            inputStream = I18n.getResource(clazz, basePath + alternativeExt);
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
