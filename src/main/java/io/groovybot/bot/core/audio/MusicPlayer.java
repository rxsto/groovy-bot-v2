package io.groovybot.bot.core.audio;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.util.EmbedUtil;
import io.groovybot.bot.util.SafeMessage;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer extends Player {

    private LavalinkManager lavalinkManager;
    private final Guild guild;

    public MusicPlayer(Guild guild) {
        super();
        this.lavalinkManager = GroovyBot.getInstance().getLavalinkManager();
        this.guild = guild;
        instanciatePlayer(lavalinkManager.getLavalink().getLink(guild));
    }

    public void connect(VoiceChannel channel) {
        link.connect(channel);
    }

    public boolean checkConnect(CommandEvent event) {
        if (!event.getGuild().getSelfMember().hasPermission(event.getMember().getVoiceState().getChannel(), Permission.VOICE_CONNECT, Permission.VOICE_SPEAK)) {
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(event.translate("phrases.join.nopermission.title"), event.translate("phrases.join.nopermission.description")));
            return false;
        }
        return true;
    }

    public void leave() {
        trackQueue.clear();
        player.stopTrack();
        link.disconnect();
    }

    @Override
    public void disconnect() {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (guild.getSelfMember().getVoiceState().getChannel().getMembers().isEmpty())
                            leave();

                    }
                }
        ,60*5*100);
    }

    @Override
    protected void save() {
        GroovyBot.getInstance().getMusicPlayerManager().update(guild, this);
    }
}
