package io.groovybot.bot.core.command;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.command.permission.UserPermissions;
import io.groovybot.bot.core.entity.EntityProvider;
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
}
