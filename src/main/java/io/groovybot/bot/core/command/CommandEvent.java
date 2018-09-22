package io.groovybot.bot.core.command;

import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.Event;

@Getter
public class CommandEvent extends Event {

    private final GroovyBot groovyBot;
    private final String[] args;
    private final String invocation;
    private final Message message;
    private final TextChannel channel;
    private final User author;
    private final Guild guild;

    public CommandEvent(JDA api, long responseNumber, Message message, GroovyBot groovyBot, String[] args, String invocation) {
        super(api, responseNumber);
        this.channel = message.getTextChannel();
        this.author = message.getAuthor();
        this.guild = message.getGuild();
        this.message = message;
        this.groovyBot = groovyBot;
        this.args = args;
        this.invocation = invocation;
    }

    public String translate(String key) {
        return groovyBot.getTranslationManager().getLocaleByUser(author.getId()).translate(key);
    }

    public Member getMember() {
        return message.getMember();
    }

}
