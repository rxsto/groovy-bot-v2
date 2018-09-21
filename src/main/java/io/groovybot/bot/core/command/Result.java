package io.groovybot.bot.core.command;

import io.groovybot.bot.util.SafeMessage;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

@RequiredArgsConstructor
public class Result {

    private final Message message;

    public Result(MessageBuilder messageBuilder) {
        this(messageBuilder.build());
    }

    public Result(MessageEmbed messageEmbed) {
        this(new MessageBuilder().setEmbed(messageEmbed));
    }

    public Result(String message) {
        this(new MessageBuilder().setContent(message));
    }

    public Result(EmbedBuilder embedBuilder) {
        this(embedBuilder.build());
    }

    public void sendMessage(TextChannel channel, Integer delTime) {
        SafeMessage.sendMessage(channel, message, delTime);
    }
}
