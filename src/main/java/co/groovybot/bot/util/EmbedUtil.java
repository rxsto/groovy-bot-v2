/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.util;

import co.groovybot.bot.core.command.CommandEvent;
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
     * Creates an noTitle embed
     *
     * @param description The description of the embed
     * @return an EmbedBuiler
     */
    public static EmbedBuilder noTitle(String description) {
        return new EmbedBuilder().setDescription(description).setColor(Colors.DARK_BUT_NOT_BLACK);
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
