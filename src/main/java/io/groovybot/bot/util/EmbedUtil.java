package io.groovybot.bot.util;

import io.groovybot.bot.core.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;

public class EmbedUtil extends SafeMessage {

    public static EmbedBuilder success(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":white_check_mark: " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    public static EmbedBuilder error(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":x: " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    public static EmbedBuilder error(CommandEvent event) {
        return error(event.translate("phrases.error.unknown.title"), event.translate("phrases.error.unknown.description"));
    }

    public static EmbedBuilder info(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":information_source: " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    public static EmbedBuilder play(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":notes: " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    public static EmbedBuilder join(String title, String description, boolean joined) {
        return new EmbedBuilder().setDescription(description).setTitle(String.format("%s ", joined ? ":white_check_mark:" : ":x:") + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }
}
