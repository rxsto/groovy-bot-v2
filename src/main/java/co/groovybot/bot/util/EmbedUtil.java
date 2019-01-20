/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;

import java.time.Instant;

public class EmbedUtil extends SafeMessage {

    /**
     * Creates a success embed
     *
     * @param title       The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuiler
     */
    public static EmbedBuilder success(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(String.format("<:success:535827110552666112> %s", title)).setColor(Colors.GREEN);
    }

    /**
     * Creates an error embed
     *
     * @param title       The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuiler
     */
    public static EmbedBuilder error(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(String.format("<:error:535827110489620500> %s", title)).setColor(Colors.RED);
    }

    /**
     * Creates an error embed
     *
     * @param event The event of the command where the error was thrown
     * @return an EmbedBuilder
     */
    public static EmbedBuilder error(CommandEvent event) {
        return error(event.translate("phrases.error"), event.translate("phrases.error.unknown"));
    }

    /**
     * Creates an info embed
     *
     * @param title       The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuiler
     */
    public static EmbedBuilder info(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(String.format("<:info:535828529573789696> %s", title)).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    /**
     * Creates a play embed
     *
     * @param title       The title of the embed
     * @param description The description of the embed
     * @return an EmbedBuiler
     */
    public static EmbedBuilder play(String title, String description, long duration) {
        return new EmbedBuilder().setDescription(description).setTitle(String.format("<:playing:535833712181510164> %s", title)).setColor(Colors.DARK_BUT_NOT_BLACK).setFooter("Duration: " + FormatUtil.formatDuration(duration), null);
    }

    /**
     * Creates a small embed
     *
     * @param description The description of the embed
     * @return an EmbedBuiler
     */
    public static EmbedBuilder small(String description) {
        return new EmbedBuilder().setDescription(description).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    /**
     * Creates a welcome embed
     *
     * @return an EmbedBuiler
     */
    public static EmbedBuilder welcome(Guild guild) {
        String prefix = GroovyBot.getInstance().getConfig().getJSONObject("settings").getString("prefix");
        String title = String.format("**Hey!** My name is %s, and I'm the best bot on Discord! **Seriously.**", guild.getJDA().getSelfUser().getName());
        String description = String.format("**-** %s's prefix is **`%s`**", guild.getJDA().getSelfUser().getName(), prefix) + "\n" + String.format("**-** You want to customize %s? Change his prefix with **`%sprefix`**", guild.getJDA().getSelfUser().getName(), prefix) + "\n" + String.format("**-** %s got an enormous amount of features! List all commands with **`%shelp`**", guild.getJDA().getSelfUser().getName(), prefix) + "\n" + String.format("**-** You wanna start listening to some good music right now? Use **`%splay`**", prefix) + "\n" + "**-** Need help? Join the support at **https://discord.gg/5s5TsW2**";
        String footer = "https://groovybot.co";
        return new EmbedBuilder().setTitle(title).setDescription(description).setFooter(footer, null).setColor(Colors.DARK_BUT_NOT_BLACK).setTimestamp(Instant.now());
    }
}
