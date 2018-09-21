package io.groovybot.bot.util;

import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class EmbedUtil extends SafeMessage {

    public static EmbedBuilder success(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":white_check_mark: " + title).setColor(Color.GREEN);
    }

    public static EmbedBuilder error(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":x: " + title).setColor(Color.RED);
    }

    public static EmbedBuilder info(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":information_source: " + title).setColor(Color.BLUE);
    }
}
