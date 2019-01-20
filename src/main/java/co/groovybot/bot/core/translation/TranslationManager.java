/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.core.translation;

import co.groovybot.bot.core.entity.EntityProvider;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Log4j2
public class TranslationManager {

    @Getter
    private final List<TranslationLocale> locales;
    @Getter
    private final TranslationLocale defaultLocale;

    public TranslationManager() {
        this.defaultLocale = new TranslationLocale(this, new Locale("en", "US"), "English (United States)") {
            @Override
            public String translate(String key) {
                if (getResourceBundle().containsKey(key))
                    return getResourceBundle().getString(key);
                else {
                    log.error(String.format("TranslationLocale for '%s' missing in default locale %s", key, defaultLocale.getLanguageName()));
                    return String.format("Missing translation for %s.", key);
                }
            }
        };

        locales = new ArrayList<>();
        locales.add(defaultLocale);
        locales.add(new TranslationLocale(this, new Locale("fr", "FR"), "French (France)"));
        locales.add(new TranslationLocale(this, new Locale("zh", "CN"), "Chinese (Simplified)"));
        locales.add(new TranslationLocale(this, new Locale("zh", "TW"), "Chinese (Traditional)"));
        //locales.add(new TranslationLocale(this, new Locale("nl", "NL"), "Dutch (Netherlands)"));
        //locales.add(new TranslationLocale(this, new Locale("de", "DE"), "German (Germany)"));
    }

    public TranslationLocale getLocaleByLocale(Locale locale) {
        return locales.parallelStream().filter(locale1 -> locale1.getLocale().equals(locale)).collect(Collectors.toList()).get(0);
    }

    public boolean isTranslated(Locale locale) {
        return !locales.parallelStream().filter(locale1 -> locale1.getLocale().equals(locale)).collect(Collectors.toList()).isEmpty();
    }

    public TranslationLocale getLocaleByUser(String userId) {
        return getLocaleByLocale(EntityProvider.getUser(Long.parseLong(userId)).getLocale());
    }
}
