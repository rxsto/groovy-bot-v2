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

package co.groovybot.bot.core.command;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.command.permission.UserPermissions;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.core.entity.Guild;
import co.groovybot.bot.core.entity.User;
import lombok.Getter;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

@Getter
public class CommandEvent extends GuildMessageReceivedEvent {

    private final GroovyBot bot;
    private final String[] args;
    private final String invocation;
    private final UserPermissions permissions;

    public CommandEvent(GuildMessageReceivedEvent event, GroovyBot bot, String[] args, String invocation) {
        super(event.getJDA(), event.getResponseNumber(), event.getMessage());
        this.bot = bot;
        this.args = args;
        this.invocation = invocation;
        this.permissions = EntityProvider.getUser(getAuthor().getIdLong()).getPermissions();
    }

    /**
     * Returns the translation of a key
     *
     * @param key the key of the translation
     * @return the translation as a String
     */
    public String translate(String key) {
        return bot.getTranslationManager().getLocaleByUser(getAuthor().getId()).translate(key);
    }

    public String getArguments() {
        return String.join(" ", args);
    }

    /**
     * @return the Groovy user instance
     */
    public User getGroovyUser() {
        return EntityProvider.getUser(getAuthor().getIdLong());
    }

    /**
     * @return the Groovy guild instance
     */
    public Guild getGroovyGuild() {
        return EntityProvider.getGuild(getGuild().getIdLong());
    }

    /**
     * @return Whether there are args or not
     */
    public boolean noArgs() {
        return args.length == 0;
    }

}
