package io.groovybot.bot.core.command.interaction;

import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

public abstract class InteractableMessage {

    @Getter
    private final Message infoMessage;
    @Getter
    private final TextChannel channel;
    @Getter
    private final Member author;
    @Getter
    private final long identifier;

    public InteractableMessage(Message infoMessage, TextChannel channel, Member author, long identifier) {
        this.infoMessage = infoMessage;
        this.channel = channel;
        this.author = author;
        this.identifier = identifier;
        GroovyBot.getInstance().getInteractionManager().register(this);
    }

    protected void update() {
        GroovyBot.getInstance().getInteractionManager().update(this);
    }

    protected void unregister() {
        onDelete();
        GroovyBot.getInstance().getInteractionManager().unregister(this);
    }

    protected void handleReaction(GuildMessageReactionAddEvent event) {

    }

    protected void handleMessage(GuildMessageReceivedEvent event) {

    }

    protected String translate(User user, String key) {
        return GroovyBot.getInstance().getTranslationManager().getLocaleByUser(user.getId()).translate(key);
    }

    public void onDelete() {
        //Empty method
    }
}
