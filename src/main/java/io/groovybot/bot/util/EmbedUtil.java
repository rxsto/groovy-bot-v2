package io.groovybot.bot.util;

import io.groovybot.bot.core.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;

public class EmbedUtil extends SafeMessage {

    /**
     * Creates an success embed
     *
     * @param title       The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuiler
     */
    public static EmbedBuilder success(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle("✅ " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    /**
     * Creates an error embed
     *
     * @param title       The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuiler
     */
    public static EmbedBuilder error(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle("❌ " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    /**
     * Creates an error embed
     *
     * @param event The event of the command where the error was thrown
     * @return an EmbedBuilder
     */
    public static EmbedBuilder error(CommandEvent event) {
        return error(event.translate("phrases.error.unknown.title"), event.translate("phrases.error.unknown.description"));
    }

    /**
     * Creates an info embed
     *
     * @param title       The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuiler
     */
    public static EmbedBuilder info(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle("ℹ " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    /**
     * Creates an play embed
     *
     * @param title       The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuiler
     */
    public static EmbedBuilder play(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle("🎶 " + title).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    /**
     * Creates an welcome embed
     *
     * @param guild The guild Groovy joined
     * @return an EmbedBuiler
     */
    public static EmbedBuilder welcome(Guild guild) {
        String title = "\uD83C\uDFB6 **Hey, I'm Groovy, the best music-bot on Discord!**";
        String description = title + "\n" + "▫ My **prefix** on this guild **is** **`g!`**\n▫ **Change** my **prefix** with **`g!prefix`**\n▫ For a **list** of **all commands** type **`g!help`**\n▫ You **want** to **play** music? **Right now?** Try **`g!play`**\n▫ **Join** our **support-server** at **https://discord.gg/5s5TsW2**";
        return new EmbedBuilder().setDescription(description).setColor(Colors.DARK_BUT_NOT_BLACK).setThumbnail(guild.getSelfMember().getUser().getAvatarUrl()).setFooter("Let's enjoy some good music!", guild.getSelfMember().getUser().getAvatarUrl());
    }
}
