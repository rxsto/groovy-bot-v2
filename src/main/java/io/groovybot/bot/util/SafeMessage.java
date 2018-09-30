package io.groovybot.bot.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SafeMessage {

    private static RestAction<Message> getAction(TextChannel channel, Message message) {
        if (hasWritePermissions(channel))
            if (hasEmbedPermissions(channel) || message.getEmbeds().isEmpty())
                return channel.sendMessage(message);
            else
                return channel.sendMessage(formatEmbed(message));
        return channel.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage(String.format("I am unable to write on your server in channel %s", channel.getName()));
    }

    public static void sendMessage(TextChannel channel, Message message) {
        Objects.requireNonNull(getAction(channel, message)).queue();
    }

    public static void sendMessage(TextChannel channel, EmbedBuilder builder) {
        getAction(channel, new MessageBuilder().setEmbed(builder.build()).build()).queue();
    }

    public static void sendMessage(TextChannel channel, Message message, Integer delTime) {
        Objects.requireNonNull(getAction(channel, message)).queue(msg -> msg.delete().queueAfter(delTime, TimeUnit.SECONDS));
    }

    public static void sendMessage(TextChannel channel, EmbedBuilder message, Integer delTime) {
        Objects.requireNonNull(getAction(channel, new MessageBuilder().setEmbed(message.build()).build())).queue(msg -> msg.delete().queueAfter(delTime, TimeUnit.SECONDS));
    }

    public static Message sendMessageBlocking(TextChannel channel, Message message) {
        return Objects.requireNonNull(getAction(channel, message)).complete();
    }

    public static Message sendMessageBlocking(TextChannel channel, EmbedBuilder embedBuilder) {
        return Objects.requireNonNull(getAction(channel, new MessageBuilder().setEmbed(embedBuilder.build()).build())).complete();
    }

    private static Message formatEmbed(Message message) {
        if (message.getEmbeds().isEmpty())
            return new MessageBuilder().setContent(message.getContentRaw()).build();
        else {
            MessageEmbed embed = message.getEmbeds().get(0);
            StringBuilder string = new StringBuilder();
            if (embed.getTitle() != null)
                string.append("**__").append(embed.getTitle()).append("__**").append("\n");
            if (embed.getDescription() != null)
                string.append(embed.getDescription());
            embed.getFields().forEach(field -> {
                string.append("**__").append(field.getName()).append("__**\n").append(field.getValue()).append("\n");
            });
            if (embed.getFooter() != null)
                string.append("\n").append("_").append(embed.getFooter().getText()).append("_");
            String out = string.toString();
            if (string.length() > 1024)
                out = "This message is longer than 1024 chars, please give me `MESSAGE_EMBED_LINKS` permission and try again";
            return new MessageBuilder().setContent(out).build();
        }
    }


    private static boolean hasWritePermissions(TextChannel channel) {
        return channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE);
    }

    private static boolean hasEmbedPermissions(TextChannel channel) {
        return channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS);
    }
}
