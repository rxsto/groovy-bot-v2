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

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
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
            return new PropertyResourceBundle(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(String.format("translation_%s_%s.properties", locale.getLanguage(), locale.getCountry()))), StandardCharsets.UTF_8));
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
