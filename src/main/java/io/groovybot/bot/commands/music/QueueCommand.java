package io.groovybot.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandCategory;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.Result;
import io.groovybot.bot.core.command.interaction.InteractableMessage;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueCommand extends Command {

    private final int PAGE_SIZE = 10;

    public QueueCommand() {
        super(new String[]{"queue", "q"}, CommandCategory.MUSIC, Permissions.everyone(), "Shows you a list of all queued songs", "");
    }

    public static EmbedBuilder formatQueue(List<AudioTrack> tracks, CommandEvent event, int startNumber, AudioTrack currentTrack, int currentPage, int totalPages) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("🎶 " + String.format(event.translate("command.queue.title"), event.getBot().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel()).getTrackQueue().size()))
                .setDescription(generateQueueDescription(tracks, startNumber, currentTrack)).setColor(Colors.DARK_BUT_NOT_BLACK);
        if (currentPage != 0 && totalPages != 0)
            builder.setFooter(String.format("%s %s", event.translate("phrases.text.site"), String.format("%s/%s", currentPage, totalPages)), null);
        return builder;
    }

    private static String generateQueueDescription(List<AudioTrack> tracks, int startNumber, AudioTrack currentTrack) {
        StringBuilder queueMessage = new StringBuilder();
        AtomicInteger trackCount = new AtomicInteger(startNumber);
        if (currentTrack != null)
            queueMessage.append(String.format("**[Now]** [%s](%s)\n\n", currentTrack.getInfo().title, currentTrack.getInfo().uri));
        tracks.forEach(track -> queueMessage.append(String.format("▫ `%s.` [%s](%s)\n", trackCount.addAndGet(1), track.getInfo().title, track.getInfo().uri)));

        return queueMessage.toString();
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        MusicPlayer player = event.getBot().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
        if (!player.isPlaying())
            return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
        if (player.getQueueSize() <= PAGE_SIZE)
            return send(formatQueue((LinkedList<AudioTrack>) player.getTrackQueue(), event, 0, player.getPlayer().getPlayingTrack(), 1, 1));
        if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE))
            return send(error(event.translate("phrases.nopermission.title"), event.translate("phrases.nopermission.manage")));
        Message infoMessage = sendMessageBlocking(event.getChannel(), info(event.translate("command.queue.loading.title"), event.translate("command.queue.loading.description")));
        new QueueMessage(infoMessage, event.getChannel(), event.getMember(), player.getTrackQueue(), event, player.getPlayer().getPlayingTrack());
        return null;
    }

    private class QueueMessage extends InteractableMessage {

        private final Queue<AudioTrack> queue;
        private final int pages;
        private final CommandEvent commandEvent;
        private final AudioTrack currentTrack;
        private int currentPage = 1;

        private QueueMessage(Message infoMessage, TextChannel channel, Member author, Queue<AudioTrack> queue, CommandEvent event, AudioTrack currentTrack) {
            super(infoMessage, channel, author, infoMessage.getIdLong());
            this.queue = queue;
            this.pages = queue.size() >= PAGE_SIZE ? queue.size() / PAGE_SIZE : 1;
            this.commandEvent = event;
            this.currentTrack = currentTrack;
            updateEmotes(true);
            updateMessage();
        }

        @Override
        protected void handleReaction(GuildMessageReactionAddEvent event) {
            switch (event.getReaction().getReactionEmote().getName()) {
                case "➡":
                    currentPage++;
                    break;
                case "⬅":
                    currentPage--;
                    break;
                default:
                    break;
            }
            updateEmotes(false);
            updateMessage();
            update();
        }

        private void updateMessage() {
            List<AudioTrack> subQueue = ((LinkedList<AudioTrack>) queue).subList((currentPage - 1) * PAGE_SIZE, ((currentPage - 1) * PAGE_SIZE + PAGE_SIZE) > queue.size() ? queue.size() : (currentPage - 1) * PAGE_SIZE + PAGE_SIZE);
            editMessage(getInfoMessage(), formatQueue(subQueue, commandEvent, (currentPage * PAGE_SIZE - 10), currentPage == 1 ? currentTrack : null, currentPage, pages + 1));
        }

        private void updateEmotes(boolean first) {
            if (!first && currentPage == 1)
                getChannel().removeReactionById(getInfoMessage().getIdLong(), "⬅").queue();
            if (currentPage > pages)
                getChannel().removeReactionById(getInfoMessage().getIdLong(), "➡").queue();
            if (currentPage > 1)
                getInfoMessage().addReaction("⬅").queue();
            if (currentPage <= pages) {
                getInfoMessage().addReaction("➡").queue();
            }
        }
    }
}
