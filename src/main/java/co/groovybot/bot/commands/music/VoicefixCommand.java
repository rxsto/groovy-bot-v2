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

public class VoicefixCommand extends SameChannelCommand {

    public VoicefixCommand() {
        super(new String[]{"voicefix", "fix"}, CommandCategory.SETTINGS, Permissions.adminOnly(), "Changes your voice region to a random one and back", "");
    }

    @Override
    public Result runCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_SERVER) && !event.getGuild().getSelfMember().hasPermission(Permission.ADMINISTRATOR))
            return send(error("phrases.nopermission.title", event.translate("phrases.nopermission.manageserver")));
        Region oldRegion = event.getGuild().getRegion();
        Message msg = SafeMessage.sendMessageBlocking(event.getChannel(), success(event.translate("command.voicefix.step1.title"), event.translate("command.voicefix.step1.description")));
        Region randomRegion = getRandomRegion(event.getGuild().getRegion());
        SafeMessage.editMessage(msg, success(event.translate("command.voicefix.step2.title"), String.format(event.translate("command.voicefix.step3.description"), randomRegion.getName())));
        event.getGuild().getManager().setRegion(randomRegion).queue(r -> {
            SafeMessage.editMessage(msg, success(event.translate("command.voicefix.step3.title"), String.format(event.translate("command.voicefix.step3.description"), oldRegion.getName())));
            event.getGuild().getManager().setRegion(oldRegion).queue(rr -> SafeMessage.editMessage(msg, success("Success!", "Your voice should be fixed now.")));
        });
        return null;
    }

    private Region getRandomRegion(Region notThis) {
        int rand = ThreadLocalRandom.current().nextInt(Region.class.getEnumConstants().length);
        Region region = Region.class.getEnumConstants()[rand];
        return region == Region.UNKNOWN || region.isVip() || region == notThis ? getRandomRegion(notThis) : region;
    }
}
