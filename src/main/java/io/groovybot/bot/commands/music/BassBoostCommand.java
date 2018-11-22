package io.groovybot.bot.commands.music;

import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SameChannelCommand;

public class BassBoostCommand extends SameChannelCommand {

    public BassBoostCommand() {
        super(new String[]{"bassboost", "bb"}, CommandCategory.MUSIC, Permissions.tierOne(), "Bass boosts songs.", "[off/low/medium/high/extreme]");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if(args.length == 0)
            return sendHelp();
        float[] bands = new float[15];
        Result result = null;
        switch (args[0]) {
            case "off":
                bands[0] = 0f;
                bands[1] = 0f;
                result = send(success(event.translate("command.bassboost.off.title"), event.translate("command.bassboost.off.description")));
                break;
            case "low":
                bands[0] = .25f;
                bands[1] = .15f;
                result = send(success(event.translate("command.bassboost.on.title"), event.translate("command.bassboost.low.description")));
                break;
            case "medium":
                bands[0] = .5f;
                bands[1] = .25f;
                result = send(success(event.translate("command.bassboost.on.title"), event.translate("command.bassboost.medium.description")));
                break;
            case "high":
                bands[0] = .75f;
                bands[1] = .5f;
                result = send(success(event.translate("command.bassboost.on.title"), event.translate("command.bassboost.high.description")));
                break;
            case "extreme":
                bands[0] = 1f;
                bands[1] = .75f;
                result = send(success(event.translate("command.bassboost.on.title"), event.translate("command.bassboost.extreme.description")));
        }
        player.getPlayer().getEqualizer().setGain(bands);
        return result;
    }
}
