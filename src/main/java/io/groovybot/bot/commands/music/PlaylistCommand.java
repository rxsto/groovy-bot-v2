package io.groovybot.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.core.audio.MusicPlayer;
import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.command.voice.SemiInChannelSubCommand;
import io.groovybot.bot.core.entity.EntityProvider;
import io.groovybot.bot.core.entity.Playlist;
import io.groovybot.bot.core.entity.User;
import io.groovybot.bot.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.Helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("duplicated")
public class PlaylistCommand extends Command {

    public PlaylistCommand() {
        super(new String[]{"playlist", "playlists"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you create own playlists", "");
        registerSubCommand(new SaveCommand());
        registerSubCommand(new AddCommand());
        registerSubCommand(new LoadCommand());
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new DeleteCommand());
        registerSubCommand(new ShowCommand());
        registerSubCommand(new ListCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0 || !getSubCommandAssociations().containsKey(args[0]))
            return sendHelp();
        return null;
    }

    private class ListCommand extends SubCommand {

        public ListCommand() {
            super(new String[]{"list"}, Permissions.everyone(), "Lists all your playlists", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            User user = EntityProvider.getUser(event.getAuthor().getIdLong());
            if (user.getPlaylists().isEmpty())
                return send(error(event.translate("command.playlists.noplaylists.title"), event.translate("command.playlists.noplaylists.description")));
            return send(buildPlaylists(user.getPlaylists().values(), event));
        }

        private EmbedBuilder buildPlaylists(Collection<Playlist> playlistCollection, CommandEvent event) {
            StringBuilder list = new StringBuilder();
            playlistCollection.forEach(playlist ->
                    list.append("- ").append(playlist.getName()).append("\n")
            );
            return info(event.translate("command.playlists.title"), list.toString());
        }
    }

    private class DeleteCommand extends SubCommand {

        public DeleteCommand() {
            super(new String[]{"delete", "del"}, Permissions.everyone(), "Deletes a playlist", "<name>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length == 0)
                return sendHelp();
            User user = EntityProvider.getUser(event.getAuthor().getIdLong());
            if (!user.getPlaylists().containsKey(args[0]))
                return send(error(event.translate("command.playlist.invalid.title"), event.translate("command.playlist.invalid.description")));
            event.getBot().getPlaylistManager().deletePlaylist(args[0], event.getAuthor().getIdLong());
            return send(success(event.translate("command.playlist.deleted.title"), String.format(event.translate("command.playlist.deleted.description"), args[0])));
        }
    }

    private class RemoveCommand extends SubCommand {

        public RemoveCommand() {
            super(new String[]{"remove", "rm"}, Permissions.everyone(), "Removes a track from a playlist", "<name> <index>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length < 2)
                return sendHelp();
            if (!Helpers.isNumeric(args[1]))
                return send(error(event.translate(""), event.translate("")));
            int index = Integer.parseInt(args[1]) - 1;
            User user = EntityProvider.getUser(event.getAuthor().getIdLong());
            if (!user.getPlaylists().containsKey(args[0]))
                return send(error(event.translate("command.playlist.invalid.title"), event.translate("command.playlist.invalid.description")));
            Playlist playlist = user.getPlaylists().get(args[0]);
            if (index > playlist.getSongs().size())
                return send(error(event.translate("command.playlist.notinlist.title"), event.translate("command.playlist.notinlist.description")));
            AudioTrack track = playlist.getSongs().get(index);
            playlist.removeTrack(index);
            return send(success(event.translate("command.playlist.removed.title"), String.format(event.translate("command.playlist.removed.description"), track.getInfo().title)));
        }
    }

    public class LoadCommand extends SemiInChannelSubCommand {

        public LoadCommand() {
            super(new String[]{"load"}, Permissions.everyone(), "Loads a playlist", "<name>");
        }

        @Override
        protected Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
            if (args.length == 0)
                return sendHelp();
            User user = EntityProvider.getUser(event.getAuthor().getIdLong());
            if (!user.getPlaylists().containsKey(args[0]))
                return send(error(event.translate("command.playlist.invalid.title"), event.translate("command.playlist.invalid.description")));
            Playlist playlist = user.getPlaylists().get(args[0]);
            List<AudioTrack> songs = playlist.getSongs();
            if (!Permissions.tierTwo().isCovered(event.getPermissions(), event))
                songs = songs.stream()
                        .limit(25 - player.getQueueSize())
                        .filter(track -> track.getDuration() < 3600000)
                        .collect(Collectors.toList());
            player.queueTracks(songs.toArray(new AudioTrack[0]));
            return send(success(event.translate("command.playlist.load.title"), String.format(event.translate("command.playlist.load.description"), playlist.getName())));
        }
    }

    private class AddCommand extends SubCommand {

        public AddCommand() {
            super(new String[]{"add", "addsong"}, Permissions.everyone(), "Adds a song to a playlist", "<name> <url>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length < 2)
                return sendHelp();
            MusicPlayer player = event.getBot().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
            User user = EntityProvider.getUser(event.getAuthor().getIdLong());
            if (!user.getPlaylists().containsKey(args[0]))
                return send(error(event.translate("command.playlist.invalid.title"), event.translate("command.playlist.invalid.description")));
            String keyword = args[1];
            if (!keyword.startsWith("http://") || !keyword.startsWith("https://"))
                keyword = "ytsearch: " + keyword;
            Playlist playlist = user.getPlaylists().get(args[0]);
            if (playlist.getSongs().size() >= 25 && !Permissions.tierTwo().isCovered(event.getPermissions(), event))
                return send(error(event.translate("command.playlist.tomanysongs.title"), event.translate("command.playlist.tomanysongs.description")));
            Message infoMessage = sendMessageBlocking(event.getChannel(), EmbedUtil.info(event.translate("phrases.searching.title"), String.format(event.translate("phrases.searching.description"), args[0])));
            player.getAudioPlayerManager().loadItem(keyword, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    playlist.addTrack(track);
                    addedTrack(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    final AudioTrack track = audioPlaylist.getSelectedTrack() == null ? audioPlaylist.getTracks().get(0) : audioPlaylist.getSelectedTrack();
                    playlist.addTrack(track);
                    addedTrack(track);
                }

                @Override
                public void noMatches() {
                    editMessage(infoMessage, EmbedUtil.error(event.translate("phrases.searching.nomatches.title"), event.translate("phrases.searching.nomatches.description")));
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    editMessage(infoMessage, error(event));
                }

                private void addedTrack(AudioTrack track) {
                    editMessage(infoMessage, success(event.translate("command.playlist.added.title"), String.format(event.translate("command.playlist.added.description"), track.getInfo().title, playlist.getName())));
                }
            });
            return null;
        }
    }

    private class ShowCommand extends SubCommand {

        public ShowCommand() {
            super(new String[]{"show"}, Permissions.everyone(), "Shows you all songs of a playlist", "<name>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length == 0)
                return sendHelp();
            User user = EntityProvider.getUser(event.getAuthor().getIdLong());
            if (!user.getPlaylists().containsKey(args[0]))
                return send(error(event.translate("command.playlist.invalid.title"), event.translate("command.playlist.invalid.description")));
            Playlist playlist = user.getPlaylists().get(args[0]);
            List<AudioTrack> queuedTracks = playlist.getSongs().stream().limit(10).collect(Collectors.toList());
            return send(QueueCommand.formatQueue(queuedTracks, event, 1, null, 0, 0)
                    .setTitle(playlist.getName() + " - " + playlist.getSongs().size() + " songs"));
        }
    }

    private class SaveCommand extends SubCommand {

        public SaveCommand() {
            super(new String[]{"save", "sv", "savequeue"}, Permissions.everyone(), "Saves the queue into a playlist", "<name>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length == 0)
                return sendHelp();
            MusicPlayer player = event.getBot().getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
            if (!player.isPlaying())
                return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));
            User user = EntityProvider.getUser(event.getAuthor().getIdLong());
            if (user.getPlaylists().size() > 2 && !Permissions.tierTwo().isCovered(event.getPermissions(), event))
                return send(error(event.translate("command.playlist.tomanyplaylists.title"), event.translate("command.playlist.tomanyplaylists.description")));
            if (user.getPlaylists().containsKey(args[0]))
                return send(error(event.translate("command.playlist.exists.title"), event.translate("command.playlist.exists.description")));
            List<AudioTrack> tracks = new ArrayList<>();
            tracks.add(player.getPlayer().getPlayingTrack());
            tracks.addAll(player.trackQueue);
            Playlist playlist = event.getBot().getPlaylistManager().createPlaylist(args[0], user.getEntityId(), tracks);
            return send(success(event.translate("command.playlist.created.title"), String.format(event.translate("command.playlist.created.description"), playlist.getName())));
        }
    }
}
