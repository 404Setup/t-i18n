package one.tranic.t.i18n;

import java.util.Locale;
import java.util.Map;

// TODO: Incomplete API
public class I18nLoader {
    Map<String, String> language;
    Locale locale;
    I18n adaptar;

    public I18nLoader() {
        this(Locale.getDefault());
    }

    public I18nLoader(Locale locale) {
        setLanguage(locale);
    }

    public void reset() {
        if (language != null) {
            language.clear();
            language = null;
        }
        locale = null;
    }

    public void setLanguage(Locale locale) {
        this.locale = locale;
    }

    public void setAdaptar(I18n adaptar) {
        this.adaptar = adaptar;
    }

    public void update() {

    }
}
