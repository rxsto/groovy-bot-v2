package io.groovybot.bot.core.command;

import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.EmbedUtil;
import io.groovybot.bot.util.FormatUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.EmbedBuilder;

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

    /**
     * The methods that will be executed when the command gets invokes
     * @param args The arguments of the command
     * @param event The event of the command
     * @return a sendable Result or null
     */
    public abstract Result run(String[] args, CommandEvent event);

    /**
     * Registers a sub command
     * @param subCommand the sub commands instance
     */
    public void registerSubCommand(SubCommand subCommand) {
        subCommand.setMainCommand(this);
        Arrays.asList(subCommand.getAliases()).forEach(alias -> subCommandAssociations.put(alias, subCommand));
    }

    /**
     * Constructs a help message Result
     * @return A sendable Result
     */
    public Result sendHelp() {
        return send(FormatUtil.formatCommand(this));
    }

    /**
     * Sends a plain text Message
     * @param message The content of a message
     * @return A sendable Result
     */
    protected Result send(String message) {
        return new Result(message);
    }

    /**
     * Sends a embed
     * @param builder The builder of the embed
     * @return A sendable Result
     */
    protected Result send(EmbedBuilder builder) {
        return new Result(builder);
    }
}
