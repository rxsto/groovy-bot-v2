package io.groovybot.bot.commands.owner;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;

import java.util.Map;

public class UpdateCommand extends Command {

    public UpdateCommand() {
        super(new String[] {"update", "up"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Lets you announce an update for Groovy", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        Map<Long, MusicPlayer> players = event.getGroovyBot().getMusicPlayerManager().getPlayerStorage();

        players.forEach( (id, player) -> {
            player.update();
        });

        return send(success("Successfully announced update!", "Successfully announced update, the bot is ready to be restarted!"));
    }
}
