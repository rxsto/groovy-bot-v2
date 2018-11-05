package io.groovybot.bot.commands.owner;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
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
        event.getBot().setAllShardsInitialized(false);

        return send(success("Announcing update!", "The bot should be **ready** for being **updated** in a few seconds!"));
    }
}
