package io.groovybot.bot.commands.owner;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@Log4j2
public class UpdateCommand extends Command {

    public UpdateCommand() {
        super(new String[]{"update", "up"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Lets you announce an update for Groovy", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        Map<Long, MusicPlayer> players = event.getGroovyBot().getMusicPlayerManager().getPlayerStorage();

        players.forEach((id, player) -> {
            try {
                if (player.isPlaying())
                    player.update();
            } catch (SQLException | IOException e) {
                log.error("Error while updating the bot!", e);
            }
        });

        return send(success("Announcing update!", "The bot should be **ready** for being **updated** in a few seconds!"));
    }
}
