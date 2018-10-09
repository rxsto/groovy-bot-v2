package io.groovybot.bot.core.translation;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

@Log4j2
@Getter
public class TranslationLocale {

    private final TranslationManager translationManager;
    private final Locale locale;
    private final ResourceBundle resourceBundle;
    private final String languageName;


    public TranslationLocale(TranslationManager translationManager, Locale locale, String languageName) {
        this.translationManager = translationManager;
        this.locale = locale;
        this.resourceBundle = getBundle();
        this.languageName = languageName;
    }

    private ResourceBundle getBundle() {
        try {
            return new PropertyResourceBundle(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(String.format("translation_%s_%s.properties", locale.getLanguage(), locale.getCountry())), StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.catching(e);
            return null;
        }
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
