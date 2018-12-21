package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.util.EmbedUtil;

public class BassBoostCommand extends SameChannelCommand {

    public BassBoostCommand() {
        super(new String[]{"bassboost", "bb"}, CommandCategory.MUSIC, Permissions.tierTwo(), "Lets you toggle the bassboost", "[off/low/medium/high/extreme]");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return send(EmbedUtil.success(event.translate("command.bassboost.info.title"), String.format(event.translate("command.bassboost.info.description"), player.getBassboost())));
        float[] bands = new float[15];
        switch (args[0]) {
            case "off":
                bands[0] = 0f;
                bands[1] = 0f;
                break;
            case "low":
                bands[0] = .25f;
                bands[1] = .15f;
                break;
            case "medium":
                bands[0] = .5f;
                bands[1] = .25f;
                break;
            case "high":
                bands[0] = .75f;
                bands[1] = .5f;
                break;
            case "extreme":
                bands[0] = 1f;
                bands[1] = .75f;
                break;
            default:
                return sendHelp();
        }
        player.getPlayer().getEqualizer().setGain(bands);
        player.setBassboost(args[0]);
        return send(EmbedUtil.success(event.translate("command.bassboost.title"), String.format(event.translate("command.bassboost.description"), args[0])));
    }
}
