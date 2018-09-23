package io.groovybot.bot.util;

import io.groovybot.bot.core.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class EmbedUtil extends SafeMessage {

    public static EmbedBuilder success(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":white_check_mark: " + title).setColor(0x2C2F33);
    }

    public static EmbedBuilder error(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":x: " + title).setColor(0x2C2F33);
    }

    public static EmbedBuilder error(CommandEvent event) {
        return error(event.translate("phrases.unknownerror.title"), event.translate("phrases.unknownerror.description"));
    }

    public static EmbedBuilder info(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":information_source: " + title).setColor(0x2C2F33);
    }

    public static EmbedBuilder play(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":notes: " + title).setColor(0x2C2F33);
    }
}
