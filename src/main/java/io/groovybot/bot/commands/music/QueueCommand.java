package io.groovybot.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueCommand extends Command {

    private final int PAGE_SIZE = 10;

    public QueueCommand() {
        super(new String[] {"queue", "q"}, CommandCategory.MUSIC, Permissions.everyone(), "Shows you each song inside the queue", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        MusicPlayer player = event.getGroovyBot().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        if (player.getQueueSize() <= PAGE_SIZE)  
            return send(formatQueue(player.getTrackQueue(), event, 0, player.getPlayer().getPlayingTrack()));
        return null;
    }

    private EmbedBuilder formatQueue(Queue<AudioTrack> tracks, CommandEvent event, int startNumber, AudioTrack currentTrack) {
        return new EmbedBuilder().setTitle(event.translate("command.queue.title"))
            .setDescription(generateQueueDescription(tracks, startNumber, currentTrack)).setColor(Colors.DARK_BUT_NOT_BLACK);
    }

    private String generateQueueDescription(Queue<AudioTrack> tracks, int startNumber, AudioTrack currentTrack) {
        StringBuilder queueMessage = new StringBuilder();
        AtomicInteger trackCount = new AtomicInteger(startNumber);
        queueMessage.append(String.format("**[Now]** [%s](%s)\n\n", currentTrack.getInfo().title, currentTrack.getInfo().uri));
        tracks.forEach(track -> {
            queueMessage.append(String.format(":white_small_square: `%s.` [%s](%s)\n", trackCount.addAndGet(1), track.getInfo().title, track.getInfo().uri));
        });
        
        return queueMessage.toString();
    }
}
