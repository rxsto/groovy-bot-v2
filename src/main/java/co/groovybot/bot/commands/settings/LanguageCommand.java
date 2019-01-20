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

package co.groovybot.bot.commands.settings;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.core.entity.entities.GroovyUser;
import co.groovybot.bot.core.translation.TranslationManager;
import co.groovybot.bot.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Locale;

public class LanguageCommand extends Command {

    public LanguageCommand() {
        super(new String[]{"language", "lang"}, CommandCategory.SETTINGS, Permissions.everyone(), "Lets you set your language", "[language-tag]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        GroovyUser groovyUser = EntityProvider.getUser(event.getAuthor().getIdLong());

        if (args.length == 0)
            return send(new EmbedBuilder().setColor(Colors.DARK_BUT_NOT_BLACK).addField(event.translate("command.language.current"), String.format(" - %s `%s`", groovyUser.getLocale().getDisplayName(), groovyUser.getLocale()), false).addField(event.translate("command.language.supported"), formatAvailableLanguages(event.getBot().getTranslationManager()), false));

        Locale locale;

        try {
            locale = Locale.forLanguageTag(args[0].replace("_", "-"));
        } catch (Exception e) {
            return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.language")));
        }

        if (!event.getBot().getTranslationManager().isTranslated(locale))
            return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.language")));

        groovyUser.setLocale(locale);

        return send(success(event.translate("phrases.success"), String.format(event.translate("command.language"), locale.getDisplayName())));
    }

    private String formatAvailableLanguages(TranslationManager translationManager) {
        StringBuilder builder = new StringBuilder();
        translationManager.getLocales().forEach(locale -> builder.append(" - ").append(locale.getLanguageName()).append(" `").append(locale.getLocale().toLanguageTag().replace("-", "_")).append("`").append("\n"));
        return builder.toString();
    }
}
