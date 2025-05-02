package one.tranic.t.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A utility class for loading and managing internationalized translations through a language map.
 * Provides methods for setting the locale, resetting the state, formatting strings, and converting
 * translation keys into various component types for localized output.
 * <p>
 * You need to manually install the dependent library.
 *
 * @see <a href="https://github.com/404Setup/t-i18n#Installation">Installation</a>
 */
@SuppressWarnings("unused")
public class I18nLoader {
    private static final boolean v1;

    static {
        boolean v2 = false;
        try {
            Class.forName("one.tranic.t.utils.Collections");
            v2 = true;
        } catch (Exception ignored) {
        }
        v1 = v2;
    }

    private final @NotNull Map<String, String> language;
    private final @Nullable String namespace;
    private final @Nullable Path path;
    private final @Nullable File file;
    private final @NotNull I18n adaptar;
    private final @Nullable Class<?> clazz;
    private @Nullable Locale locale;

    public I18nLoader(@NotNull File file, @NotNull I18n adaptar) {
        this(file, null, null, null, null, adaptar);
    }

    public I18nLoader(@NotNull String namespace, @NotNull I18n adaptar) throws IllegalArgumentException {
        this(null, null, null, namespace, null, adaptar);
    }

    public I18nLoader(@NotNull Path path, @NotNull I18n adaptar) throws IllegalArgumentException {
        this(null, path, null, null, null, adaptar);
    }

    public I18nLoader(@NotNull Class<?> clazz, @NotNull String namespace, @NotNull I18n adaptar) throws IllegalArgumentException {
        this(null, null, clazz, namespace, Locale.ENGLISH, adaptar);
    }

    public I18nLoader(@NotNull Class<?> clazz, @NotNull String namespace, @NotNull Locale locale, @NotNull I18n adaptar) throws IllegalArgumentException {
        this(null, null, clazz, namespace, locale, adaptar);
    }

    public I18nLoader(@Nullable File file, @Nullable Path path, @Nullable Class<?> clazz, @Nullable String namespace, @Nullable Locale locale, @NotNull I18n adaptar) throws IllegalArgumentException {
        this.file = file;
        this.clazz = file == null ? clazz : null;
        this.locale = locale == null ? Locale.ENGLISH : locale;
        this.namespace = namespace;

        if (this.clazz != null && this.namespace == null) {
            throw new IllegalArgumentException("Namespace must not be null when loading from a class");
        }

        if (this.clazz == null && this.namespace != null) {
            this.path = Path.of(this.namespace);
            if (!Files.exists(this.path)) {
                throw new IllegalArgumentException("Namespace does not exist: " + this.namespace);
            }
        } else {
            this.path = path;
        }

        this.language = createMap();
        this.adaptar = adaptar;
    }

    public static <K, V> Map<K, V> createMap() {
        return v1 ? one.tranic.t.utils.Collections.newHashMap() : new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("I18nLoader{");
        int size = language.size();
        if (size < 15)
            sb.append("language=").append(language).append(", ");
        sb.append("languageSize=").append(size);
        if (namespace != null)
            sb.append(", namespace='").append(namespace).append('\'');
        if (clazz != null)
            sb.append(", class=").append(clazz);
        if (file != null)
            sb.append(", file='").append(file).append('\'');
        if (locale != null)
            sb.append(", locale=").append(locale.getLanguage());
        sb.append(", adaptar=").append(adaptar).append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = language.hashCode();
        result = 31 * result + (namespace != null && !namespace.isBlank() ? namespace.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + adaptar.hashCode();
        return result;
    }

    /**
     * Resets the current state of the language map and assigns a new locale for this instance.
     *
     * @param locale the Locale to be set; it can be null to indicate no specific locale
     */
    public void reset(@Nullable Locale locale) {
        language.clear();
        this.locale = locale;
    }

    /**
     * Resets the current state of the object to its default configuration
     * using the predefined locale.
     */
    public void reset() {
        this.reset(this.locale);
    }

    /**
     * Updates the internal language map with the default behavior.
     *
     * @throws IOException if an I/O error occurs during loading of the localized strings.
     */
    @SuppressWarnings("ConstantConditions")
    public void update() throws IOException {
        this.update(this.locale);
    }

    /**
     * Updates the internal language map based on the provided locale.
     *
     * @param locale the locale to be used for loading the language map. Must not be null.
     * @throws IOException if no valid configuration is available for loading the language map,
     *                     or if an I/O error occurs during the loading process.
     */
    public void update(@NotNull Locale locale) throws IOException {
        this.locale = locale;
        Map<String, String> lang;
        if (this.file != null) {
            lang = this.adaptar.load(this.file);
        } else if (this.path != null) {
            lang = this.adaptar.load(this.path, locale);
        } else if (this.clazz != null && this.namespace != null) {
            lang = this.adaptar.load(this.clazz, this.namespace, locale);
        } else {
            throw new IOException("Invalid configuration for loading language map");
        }
        this.language.clear();
        this.language.putAll(lang);
    }

    /**
     * Updates the current language map using the data from the provided input stream.
     *
     * @param customInputStream the input stream containing the new language data; must not be null
     * @throws IOException if an I/O error occurs during the loading of data from the input stream
     */
    public void update(@NotNull InputStream customInputStream) throws IOException {
        var lang = this.adaptar.load(customInputStream);
        this.language.clear();
        this.language.putAll(lang);
    }

    /**
     * Return a copy of the language map
     *
     * @return a non-null copy of the language map
     */
    public @NotNull Map<String, String> getLanguageMap() {
        return v1 ? one.tranic.t.utils.Collections.newHashMap(language) : new HashMap<>(language);
    }

    /**
     * Retrieves the current language setting as a Locale object.
     *
     * @return the Locale object representing the current language setting, or null if no language is set.
     */
    public @Nullable Locale getLanguage() {
        return locale;
    }

    /**
     * Sets the language for localized operations.
     *
     * @param locale the {@link Locale} object representing the desired language and region settings; must not be null
     */
    public void setLanguage(@NotNull Locale locale) {
        this.locale = locale;
    }

    /**
     * Translates the provided key into a localized string using the predefined language map.
     * <p>
     * If the key is not found in the language map, the key itself is returned as the fallback value.
     *
     * @param key the translation key used to fetch the corresponding localized string
     * @return the localized string corresponding to the provided key, or the key itself if no translation exists
     */
    public @NotNull String to(@NotNull String key) {
        return language.getOrDefault(key, key);
    }

    /**
     * Retrieves a formatted translation for the specified key using the provided arguments.
     * <p>
     * The format follows printf-style format strings, for example:
     * <pre>
     * "Hello %s" with arg "world" produces "Hello world"
     * "%d items" with arg 5 produces "5 items"
     * "Rate: %.2f" with arg 0.123 produces "Rate: 0.12"
     * </pre>
     *
     * @param key  the translation key used to fetch the corresponding localized string
     * @param args the arguments to format the localized string
     * @return the formatted translated string
     */
    public @NotNull String to(@NotNull String key, @NotNull Object... args) {
        var text = to(key);
        if (args.length == 0 || key.equals(text)) return text;
        return String.format(text, args);
    }

    /**
     * Replaces placeholders in the localized string corresponding to the provided key with the provided arguments.
     * <p>
     * For example:
     * <pre>
     * "{} items in {}" with args ["5", "cart"] produces "5 items in cart"
     * "Hello {}" with arg "world" produces "Hello world"
     * "Rate: {}" with arg "0.12" produces "Rate: 0.12"
     * </pre>
     *
     * @param key  the translation key used to fetch the corresponding localized string
     * @param args the arguments to replace the "{}" placeholders within the localized string
     * @return the localized and formatted string with placeholders replaced by the arguments, or the key itself if no translation exists
     */
    public @NotNull String toBrace(@NotNull String key, @NotNull Object... args) {
        String text = to(key);
        if (args.length == 0 || key.equals(text)) return text;

        StringBuilder sb = new StringBuilder(text);
        String placeholder = "{}";
        int placeholderIndex = 0;
        int searchFrom = 0;

        while ((searchFrom = sb.indexOf(placeholder, searchFrom)) != -1 && placeholderIndex < args.length) {
            sb.replace(searchFrom, searchFrom + placeholder.length(), String.valueOf(args[placeholderIndex]));
            searchFrom += String.valueOf(args[placeholderIndex]).length();
            placeholderIndex++;
        }

        return sb.toString();
    }

    /**
     * Replaces placeholders in a text template with corresponding values from the provided components.
     *
     * <p>
     * For example:
     * <pre>
     * "{b1} items in {b2}" with args {"b1": "5", "b2", "cart"} produces "5 items in cart"
     * "Hello {b1}" with arg {"b1", "world"} produces "Hello world"
     * "Rate: {b1}" with arg {"b1", 0.12} produces "Rate: 0.12"
     * </pre>
     *
     * @param key  the text template containing placeholders in the form of "{keyword}"
     * @param args an array of {@link SimpleComponent} objects that provide the values for placeholders
     * @return the updated text with placeholders replaced by their corresponding values;
     * returns the original text if no placeholders match or if no components are provided
     */
    public @NotNull String toBrace(@NotNull String key, @NotNull SimpleComponent... args) {
        String text = to(key);
        if (args.length == 0 || key.equals(text)) return text;

        StringBuilder sb = new StringBuilder(text);
        for (SimpleComponent component : args) {
            String placeholder = "{" + component.keyword() + "}";
            int placeholderIndex;
            while ((placeholderIndex = sb.indexOf(placeholder)) != -1) {
                sb.replace(placeholderIndex, placeholderIndex + placeholder.length(), String.valueOf(component.value()));
            }
        }
        return sb.toString();
    }

    /**
     * Converts a localized string corresponding to the given key into an array of BaseComponent objects.
     *
     * @param key the translation key used to fetch the corresponding localized string
     * @return an array of BaseComponent objects representing the localized string,
     * or the key itself as BaseComponents if no translation exists
     */
    public @NotNull net.md_5.bungee.api.chat.BaseComponent[] toBaseComponent(@NotNull String key) {
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText(to(key));
    }

    /**
     * Converts a translation key and its formatted arguments into an array of {@link net.md_5.bungee.api.chat.BaseComponent}.
     * <p>
     * The format follows printf-style format strings, for example:
     * <pre>
     * "Hello %s" with arg "world" produces "Hello world"
     * "%d items" with arg 5 produces "5 items"
     * "Rate: %.2f" with arg 0.123 produces "Rate: 0.12"
     * </pre>
     *
     * @param key  the translation key used to fetch the corresponding localized string
     * @param args the arguments to format the localized string
     * @return an array of {@link net.md_5.bungee.api.chat.BaseComponent} representing the localized and formatted text
     */
    public @NotNull net.md_5.bungee.api.chat.BaseComponent[] toBaseComponent(@NotNull String key, @NotNull Object... args) {
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText(to(key, args));
    }

    /**
     * Converts a localized and formatted string with placeholders replaced by the provided arguments
     * into an array of {@link net.md_5.bungee.api.chat.BaseComponent}.
     * <p>
     * For example:
     * <pre>
     * "{} items in {}" with args ["5", "cart"] produces "5 items in cart"
     * "Hello {}" with arg "world" produces "Hello world"
     * "Rate: {}" with arg "0.12" produces "Rate: 0.12"
     * </pre>
     *
     * @param key  the translation key used to fetch the corresponding localized string
     * @param args the arguments to replace the "{}" placeholders within the localized string
     * @return an array of {@link net.md_5.bungee.api.chat.BaseComponent} representing the processed localized string
     */
    public @NotNull net.md_5.bungee.api.chat.BaseComponent[] toBaseComponentBrace(@NotNull String key, @NotNull Object... args) {
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText(toBrace(key, args));
    }

    /**
     * Converts the provided key and arguments into an array of BaseComponent objects
     * using a legacy text format with braces.
     *
     * <p>
     * For example:
     * <pre>
     * "{b1} items in {b2}" with args {"b1": "5", "b2", "cart"} produces "5 items in cart"
     * "Hello {b1}" with arg {"b1", "world"} produces "Hello world"
     * "Rate: {b1}" with arg {"b1", 0.12} produces "Rate: 0.12"
     * </pre>
     *
     * @param key  the key to be formatted, must not be null
     * @param args the array of SimpleComponent arguments to format, must not be null
     * @return an array of BaseComponent objects representing the formatted text
     */
    public @NotNull net.md_5.bungee.api.chat.BaseComponent[] toBaseComponentBrace(@NotNull String key, @NotNull SimpleComponent... args) {
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText(toBrace(key, args));
    }

    /**
     * Converts the provided translation key into an Adventure Component.
     *
     * @param key the translation key used to fetch the corresponding localized string
     * @return an Adventure Component containing the localized string for the given translation key,
     * or the key itself if no translation exists
     */
    public @NotNull net.kyori.adventure.text.Component toComponent(@NotNull String key) {
        return net.kyori.adventure.text.Component.text(to(key));
    }

    /**
     * Converts the translation key and its formatted arguments into an Adventure Component.
     * <p>
     * The format follows printf-style format strings, for example:
     * <pre>
     * "Hello %s" with arg "world" produces "Hello world"
     * "%d items" with arg 5 produces "5 items"
     * "Rate: %.2f" with arg 0.123 produces "Rate: 0.12"
     * </pre>
     *
     * @param key  the translation key used to fetch the corresponding localized string
     * @param args the arguments to format the localized string
     * @return an Adventure Component containing the localized and formatted string for the given translation key,
     * or a plain text Component containing the key if no translation exists
     */
    public @NotNull net.kyori.adventure.text.Component toComponent(@NotNull String key, @NotNull Object... args) {
        return net.kyori.adventure.text.Component.text(to(key, args));
    }

    /**
     * Converts the provided translation key and its formatted arguments into an Adventure Component.
     * <p>
     * For example:
     * <pre>
     * "{} items in {}" with args ["5", "cart"] produces "5 items in cart"
     * "Hello {}" with arg "world" produces "Hello world"
     * "Rate: {}" with arg "0.12" produces "Rate: 0.12"
     * </pre>
     *
     * @param key  the translation key used to fetch the corresponding localized string
     * @param args the arguments to replace the "{}" placeholders within the localized string
     * @return an Adventure Component containing the localized and formatted string for the given key,
     * or the key itself as a plain text Component if no translation exists
     */
    public @NotNull net.kyori.adventure.text.Component toComponentBrace(@NotNull String key, @NotNull Object... args) {
        return net.kyori.adventure.text.Component.text(toBrace(key, args));
    }

    /**
     * Converts the given key and arguments into a formatted text component using braces for placeholders.
     *
     * <p>
     * For example:
     * <pre>
     * "{b1} items in {b2}" with args {"b1": "5", "b2", "cart"} produces "5 items in cart"
     * "Hello {b1}" with arg {"b1", "world"} produces "Hello world"
     * "Rate: {b1}" with arg {"b1", 0.12} produces "Rate: 0.12"
     * </pre>
     *
     * @param key  the key used as the base text for the component must not be null
     * @param args the arguments to be placed into the placeholders within the text must not be null
     * @return a Component instance representing the formatted text
     */
    public @NotNull net.kyori.adventure.text.Component toComponentBrace(@NotNull String key, @NotNull SimpleComponent... args) {
        return net.kyori.adventure.text.Component.text(toBrace(key, args));
    }

    /**
     * Converts the translation key into a {@link net.kyori.adventure.text.Component} using MiniMessage format.
     * <p>
     * For example:
     * <pre>{@code
     * "Click <click:run_command:/help>here</click> for help" produces a clickable "here" text
     * "This is <red>red text</red>" produces text with red color
     * "Player: <player>" with player tag resolver shows player name
     * }</pre>
     *
     * @param key          the translation key used to fetch the corresponding localized string
     * @param tagResolvers optional tag resolvers used to process placeholders or tags within the localized string
     * @return the {@link net.kyori.adventure.text.Component} representation of the translated key,
     * or a plain text component containing the key if no translation exists
     * @see <a href="https://docs.advntr.dev/minimessage/format.html">MiniMessage Format</a>
     */
    public @NotNull net.kyori.adventure.text.Component toComponent(@NotNull String key, @NotNull net.kyori.adventure.text.minimessage.tag.resolver.TagResolver... tagResolvers) {
        String text = to(key);
        if (!text.equals(key))
            return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text, tagResolvers);
        return net.kyori.adventure.text.Component.text(key);
    }

    public record SimpleComponent(@NotNull String keyword, @NotNull Object value) {
    }
}
