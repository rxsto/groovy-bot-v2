package io.groovybot.bot.core.command.interaction;

import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

public abstract class InteractableMessage {

    @Getter
    private final Message infoMessage;
    @Getter
    private final TextChannel channel;
    @Getter
    private final Member author;

    public InteractableMessage(Message infoMessage, TextChannel channel, Member author) {
        this.infoMessage = infoMessage;
        this.channel = channel;
        this.author = author;
        GroovyBot.getInstance().getInteractionManager().register(this);
    }

    protected abstract void handleReaction(GuildMessageReactionAddEvent event);

    protected abstract void handleMessage(GuildMessageReceivedEvent event);

}
