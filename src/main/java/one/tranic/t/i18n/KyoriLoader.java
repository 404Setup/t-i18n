package one.tranic.t.i18n;

import one.tranic.t.i18n.loader.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

public class KyoriLoader extends BaseLoader {
    public KyoriLoader(@NotNull File file, @NotNull I18n adaptar) {
        super(file, adaptar);
    }

    public KyoriLoader(@NotNull String namespace, @NotNull I18n adaptar) throws IllegalArgumentException {
        super(namespace, adaptar);
    }

    public KyoriLoader(@NotNull Path path, @NotNull I18n adaptar) throws IllegalArgumentException {
        super(path, adaptar);
    }

    public KyoriLoader(@NotNull Class<?> clazz, @NotNull String namespace, @NotNull I18n adaptar) throws IllegalArgumentException {
        super(clazz, namespace, adaptar);
    }

    public KyoriLoader(@NotNull Class<?> clazz, @NotNull String namespace, @NotNull Locale locale, @NotNull I18n adaptar) throws IllegalArgumentException {
        super(clazz, namespace, locale, adaptar);
    }

    public KyoriLoader(@Nullable File file, @Nullable Path path, @Nullable Class<?> clazz, @Nullable String namespace, @Nullable Locale locale, @NotNull I18n adaptar) throws IllegalArgumentException {
        super(file, path, clazz, namespace, locale, adaptar);
    }

    @Override
    public String toString() {
        return toString("KyoriLoader");
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
}
