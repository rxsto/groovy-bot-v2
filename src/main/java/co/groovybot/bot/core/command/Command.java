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

import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.FormatUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public abstract class Command extends EmbedUtil {

    private final String[] aliases;
    private final CommandCategory commandCategory;
    private final Permissions permissions;
    private final String description;
    private final String usage;
    private final Map<String, SubCommand> subCommandAssociations = new HashMap<>();
    private final CommandLineParser cliParser = new DefaultParser();

    public Command(String[] aliases, CommandCategory commandCategory, Permissions permissions, String description) {
        this(aliases, commandCategory, permissions, description, "");
    }

    /**
     * The methods that will be executed when the command gets invokes
     *
     * @param args  The arguments of the command
     * @param event The event of the command
     * @return a sendable Result or null
     */
    public abstract Result run(String[] args, CommandEvent event);

    /**
     * Registers a sub command
     *
     * @param subCommand the sub commands instance
     */
    public void registerSubCommand(SubCommand subCommand) {
        subCommand.setMainCommand(this);
        Arrays.asList(subCommand.getAliases()).forEach(alias -> subCommandAssociations.put(alias, subCommand));
    }

    /**
     * Constructs a help message Result
     *
     * @return A sendable Result
     */
    public Result sendHelp() {
        return send(FormatUtil.formatCommand(this));
    }

    /**
     * Sends a plain text Message
     *
     * @param message The content of a message
     * @return A sendable Result
     */
    protected Result send(String message) {
        return new Result(message);
    }

    /**
     * Sends a embed
     *
     * @param builder The builder of the embed
     * @return A sendable Result
     */
    protected Result send(EmbedBuilder builder) {
        return new Result(builder);
    }

    public String getName() {
        return aliases[0];
    }
}
