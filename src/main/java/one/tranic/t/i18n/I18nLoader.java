package one.tranic.t.i18n;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
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
    private final Map<String, String> language;
    private final String namespace;
    private final Class<?> clazz;
    Locale locale;
    I18n adaptar;

    public I18nLoader(@Nullable Class<?> clazz, @NotNull String namespace) {
        this(clazz, namespace, Locale.ENGLISH);
    }

    public I18nLoader(@Nullable Class<?> clazz, @NotNull String namespace, @NotNull Locale locale) {
        this.clazz = clazz != null ? clazz : I18nLoader.class;
        this.locale = locale;
        this.namespace = namespace;
        this.language = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("I18nLoader{");
        int size = language.size();
        if (size < 15)
            sb.append("language=").append(language).append(", ");
        sb.append("languageSize=").append(size)
                .append(", namespace='").append(namespace).append('\'')
                .append(", clazz=").append(clazz)
                .append(", locale=").append(locale.getLanguage())
                .append(", adaptar=").append(adaptar)
                .append('}');
        return sb.toString();
    }

    /**
     * Resets the current state of the language map and assigns a new locale for this instance.
     *
     * @param locale the Locale to be set; it can be null to indicate no specific locale
     */
    public void reset(@Nullable Locale locale) {
        if (language != null) language.clear();
        this.locale = locale;
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
     * Sets the adapter used for this instance.
     *
     * @param adaptar the adapter to be assigned must not be null
     */
    public void setAdaptar(@NotNull I18n adaptar) {
        this.adaptar = adaptar;
    }

    /**
     * Updates the internal language map with localized strings for the specified class, namespace, and locale.
     *
     * @throws IOException if an I/O error occurs during loading of the localized strings.
     */
    public void update() throws IOException {
        var lang = adaptar.load(clazz, namespace, locale);
        language.clear();
        language.putAll(lang);
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
        return String.format(to(key), args);
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
