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

package co.groovybot.bot.core.events.command;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandEvent;
import lombok.Getter;

public class CommandExecutedEvent extends CommandEvent {

    @Getter
    private final Command command;

    public CommandExecutedEvent(CommandEvent event, Command command) {
        super(event, event.getBot(), event.getArgs(), event.getInvocation());
        this.command = command;
    }
}
