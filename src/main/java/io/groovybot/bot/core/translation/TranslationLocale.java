package io.groovybot.bot.core.translation;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.Locale;
import java.util.ResourceBundle;

@Log4j
@Getter
public class TranslationLocale {

    private final TranslationManager translationManager;
    private final Locale locale;
    private final ResourceBundle resourceBundle;
    private final String languageName;

    public TranslationLocale(TranslationManager translationManager, Locale locale, String languageName) {
        this.translationManager = translationManager;
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle(String.format("translation_%s_%s", locale.getLanguage(), locale.getCountry()));
        this.languageName = languageName;
    }

    public String translate(String key) {
        if (resourceBundle.containsKey(key))
            return resourceBundle.getString(key);
        else {
            log.warn(String.format("[Locale] Key %s was not found for language %s", key, locale.getLanguage()));
            return translationManager.getDefaultLocale().translate(key);
        }
    }
}
