package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SameChannelCommand;
import co.groovybot.bot.util.SafeMessage;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.ThreadLocalRandom;

public class VoiceFixCommand extends SameChannelCommand {

    public VoiceFixCommand() {
        super(new String[]{"voicefix", "fix", "vf"}, CommandCategory.MUSIC, Permissions.adminOnly(), "Lets you change your region in order to fix voice", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_SERVER))
            return send(error("phrases.nopermission", event.translate("phrases.nopermission.manageserver")));

        Region oldRegion = event.getGuild().getRegion();

        Message msg = SafeMessage.sendMessageBlocking(event.getChannel(), success(String.format(event.translate("command.voicefix.step"), "1"), event.translate("command.voicefix.step.get")));

        Region randomRegion = getRandomRegion(event.getGuild().getRegion());

        SafeMessage.editMessage(msg, success(String.format(event.translate("command.voicefix.step"), "2"), String.format(event.translate("command.voicefix.step.set"), randomRegion.getName())));

        event.getGuild().getManager().setRegion(randomRegion).queue(r -> {
            SafeMessage.editMessage(msg, success(String.format(event.translate("command.voicefix.step"), "3"), String.format(event.translate("command.voicefix.step.back"), oldRegion.getName())));
            event.getGuild().getManager().setRegion(oldRegion).queue(rr -> SafeMessage.editMessage(msg, success(event.translate("phrases.success"), event.translate("command.voicefix"))));
        });

        return null;
    }

    private Region getRandomRegion(Region notThis) {
        int rand = ThreadLocalRandom.current().nextInt(Region.class.getEnumConstants().length);
        Region region = Region.class.getEnumConstants()[rand];
        return region == Region.UNKNOWN || region.isVip() || region == notThis ? getRandomRegion(notThis) : region;
    }
}
