package one.tranic.t.i18n;

import one.tranic.t.i18n.loader.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

public class BungeeLoader extends BaseLoader {
    public BungeeLoader(@NotNull File file, @NotNull I18n adaptar) {
        super(file, adaptar);
    }

    public BungeeLoader(@NotNull String namespace, @NotNull I18n adaptar) throws IllegalArgumentException {
        super(namespace, adaptar);
    }

    public BungeeLoader(@NotNull Path path, @NotNull I18n adaptar) throws IllegalArgumentException {
        super(path, adaptar);
    }

    public BungeeLoader(@NotNull Class<?> clazz, @NotNull String namespace, @NotNull I18n adaptar) throws IllegalArgumentException {
        super(clazz, namespace, adaptar);
    }

    public BungeeLoader(@NotNull Class<?> clazz, @NotNull String namespace, @NotNull Locale locale, @NotNull I18n adaptar) throws IllegalArgumentException {
        super(clazz, namespace, locale, adaptar);
    }

    public BungeeLoader(@Nullable File file, @Nullable Path path, @Nullable Class<?> clazz, @Nullable String namespace, @Nullable Locale locale, @NotNull I18n adaptar) throws IllegalArgumentException {
        super(file, path, clazz, namespace, locale, adaptar);
    }

    @Override
    public String toString() {
        return toString("BungeeLoader");
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
}
