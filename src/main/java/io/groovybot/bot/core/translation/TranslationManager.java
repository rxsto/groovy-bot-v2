package io.groovybot.bot.core.translation;

import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Log4j
public class TranslationManager {

    @Getter
    private final List<TranslationLocale> locales;
    @Getter
    private final TranslationLocale defaultLocale;

    public TranslationManager() {
        defaultLocale = new TranslationLocale(this, new Locale("en", "US"), "English (United States)") {
            @Override
            public String translate(String key) {
                if (getResourceBundle().containsKey(key))
                    return getResourceBundle().getString(key);
                else {
                    log.error(String.format("TranslationLocale for '%s' missing in default locale %s", key, defaultLocale.getLanguageName()));
                    return "Missing translation.";
                }
            }
        };

        locales = new ArrayList<>();
        locales.add(defaultLocale);
    }

    public TranslationLocale getLocaleByLocale(Locale locale) {
        return locales.parallelStream().filter(locale1 -> locale1.getLocale().equals(locale)).collect(Collectors.toList()).get(0);
    }

    public boolean isTranslated(Locale locale) {
        return !locales.parallelStream().filter(locale1 -> locale1.getLocale().equals(locale)).collect(Collectors.toList()).isEmpty();
    }

    public TranslationLocale getLocaleByUser(String userId) {
        return getLocaleByLocale(GroovyBot.getInstance().getUserCache().get(Long.valueOf(userId)).getLocale());
    }

}
