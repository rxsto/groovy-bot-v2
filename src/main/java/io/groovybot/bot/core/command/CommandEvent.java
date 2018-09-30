package io.groovybot.bot.core.command;

import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

@Getter
public class CommandEvent extends GuildMessageReceivedEvent {

    private final GroovyBot groovyBot;
    private final String[] args;
    private final String invocation;

    public CommandEvent(GuildMessageReceivedEvent event, GroovyBot bot, String[] args, String invocation) {
        super(event.getJDA(), event.getResponseNumber(), event.getMessage());
        this.groovyBot = bot;
        this.args = args;
        this.invocation = invocation;
    }

    public String translate(String key) {
        return groovyBot.getTranslationManager().getLocaleByUser(getAuthor().getId()).translate(key);
    }

    public String getArguments() {
        return String.join(" ", args);
    }

}
