package io.groovybot.bot.core.command;

import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.EmbedUtil;
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

    public abstract Result run(String[] args, CommandEvent event);

    public void registerSubCommand(SubCommand subCommand) {
        Arrays.asList(subCommand.getAliases()).forEach(alias -> subCommandAssociations.put(alias, subCommand));
    }

    public Result sendHelp() {
        return null;
    }

    protected Result send(String message) {
        return new Result(message);
    }

    protected Result send(EmbedBuilder builder) {
        return new Result(builder);
    }
}
