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

package co.groovybot.bot.commands.owner;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@Log4j2
public class UpdateCommand extends Command {

    public UpdateCommand() {
        super(new String[]{"update"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Lets you announce an update for Groovy", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (!event.getChannel().getId().equals("508001588237828096"))
            return send(error("Forbidden!", String.format("Please use this command in %s!", event.getJDA().getTextChannelById(508001588237828096L).getAsMention())));

        Map<Long, MusicPlayer> players = event.getBot().getMusicPlayerManager().getPlayerStorage();

        players.forEach((id, player) -> {
            try {
                if (player.isPlaying())
                    player.update();
            } catch (SQLException | IOException e) {
                log.error("Error while updating the bot!", e);
            }
        });

        event.getBot().getShardManager().setStatus(OnlineStatus.DO_NOT_DISTURB);
        event.getBot().getShardManager().setGame(Game.playing("Updating ..."));

        return send(success("Announcing update!", "The bot should be **ready** for being **updated** in a few seconds!"));
    }
}
