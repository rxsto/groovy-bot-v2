/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.commands.music;

import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SemiInChannelSubCommand;
import co.groovybot.bot.core.entity.entities.GroovyPlaylist;
import co.groovybot.bot.core.entity.entities.GroovyUser;
import co.groovybot.bot.util.Colors;
import co.groovybot.bot.util.SafeMessage;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.utils.Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistCommand extends Command {

    public PlaylistCommand() {
        super(new String[]{"playlist", "playlists", "pl"}, CommandCategory.MUSIC, Permissions.everyone(), "Lets you create own playlists", "");
        registerSubCommand(new LoadCommand());
        registerSubCommand(new SaveCommand());
        registerSubCommand(new DeleteCommand());
        registerSubCommand(new RenameCommand());
        registerSubCommand(new AddCommand());
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new ListCommand());
        registerSubCommand(new SongsCommand());
        registerSubCommand(new ToggleVisibilityCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return sendHelp();
    }

    private static class LoadCommand extends SemiInChannelSubCommand {

        LoadCommand() {
            super(new String[]{"load", "l", "play"}, Permissions.everyone(), "Loads a playlist", "[user] <name>/<id>");
        }

        @Override
        protected Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
            if (args.length == 0)
                return sendHelp();

            if (event.getMessage().getMentionedMembers().size() != 0) {
                if (args.length < 2)
                    return sendHelp();

                GroovyUser groovyUser = event.getBot().getUserCache().get(event.getMessage().getMentionedMembers().get(0).getUser().getIdLong());
                String name = args[1];

                if (!groovyUser.getPlaylists().containsKey(name.toLowerCase()))
                    return send(error(event.translate("command.playlist.not.exists.title"), event.translate("command.playlist.not.exists.description")));

                if (!groovyUser.getPlaylists().get(name.toLowerCase()).isPublic())
                    return send(error(event.translate("command.playlist.not.public.title"), event.translate("command.playlist.not.public.description")));

                GroovyPlaylist groovyPlaylist = groovyUser.getPlaylists().get(name.toLowerCase());
                player.queueTracks(groovyPlaylist.getSongs().toArray(new AudioTrack[0]));
                groovyPlaylist.increaseCount();
                return send(success(event.translate("command.playlist.loaded.title"), String.format(event.translate("command.playlist.loaded.description"), groovyPlaylist.getName())));
            } else {
                if (Helpers.isNumeric(args[0]))
                    if (event.getBot().getPlaylistManager().getPlaylistById(Long.parseLong(args[0])) != null)
                        if (event.getBot().getPlaylistManager().getPlaylistById(Long.parseLong(args[0])).isPublic()) {
                            GroovyPlaylist groovyPlaylist = event.getBot().getPlaylistManager().getPlaylistById(Long.parseLong(args[0]));
                            player.queueTracks(groovyPlaylist.getSongs().toArray(new AudioTrack[0]));
                            groovyPlaylist.increaseCount();
                            return send(success(event.translate("command.playlist.loaded.title"), String.format(event.translate("command.playlist.loaded.description"), groovyPlaylist.getName())));
                        }

                GroovyUser groovyUser = event.getGroovyUser();
                String name = args[0];

                if (!groovyUser.getPlaylists().containsKey(name.toLowerCase()))
                    return send(error(event.translate("command.playlist.not.exists.title"), event.translate("command.playlist.not.exists.description")));

                GroovyPlaylist groovyPlaylist = groovyUser.getPlaylists().get(name.toLowerCase());
                player.queueTracks(groovyPlaylist.getSongs().toArray(new AudioTrack[0]));
                groovyPlaylist.increaseCount();
                return send(success(event.translate("command.playlist.loaded.title"), String.format(event.translate("command.playlist.loaded.description"), groovyPlaylist.getName())));
            }
        }
    }

    private static class SaveCommand extends SemiInChannelSubCommand {

        SaveCommand() {
            super(new String[]{"save"}, Permissions.everyone(), "Saves the queue to a playlist", "<name>");
        }

        @Override
        protected Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
            if (args.length == 0)
                return sendHelp();

            if (args.length > 1)
                return send(error(event.translate("command.playlist.invalidname.title"), event.translate("command.playlist.invalidname.title")));

            if (!player.isPlaying())
                return send(error(event.translate("phrases.notplaying.title"), event.translate("phrases.notplaying.description")));

            GroovyUser groovyUser = event.getGroovyUser();
            String name = args[0];

            if (groovyUser.getPlaylists().size() >= 5 && !Permissions.tierTwo().isCovered(event.getPermissions(), event))
                return send(error(event.translate("command.playlist.tomanyplaylists.title"), event.translate("command.playlist.tomanyplaylists.description")));

            if (groovyUser.getPlaylists().containsKey(name))
                return send(error(event.translate("command.playlist.exists.title"), event.translate("command.playlist.exists.description")));

            List<AudioTrack> tracks = new ArrayList<>();
            tracks.add(player.getPlayer().getPlayingTrack());
            tracks.addAll(player.trackQueue);
            tracks = tracks.stream()
                    .limit(10)
                    .collect(Collectors.toList());
            GroovyPlaylist groovyPlaylist = event.getBot().getPlaylistManager().createPlaylist(name, groovyUser.getEntityId(), tracks);
            return send(success(event.translate("command.playlist.created.title"), String.format(event.translate("command.playlist.created.description"), groovyPlaylist.getName())));
        }
    }

    private static class DeleteCommand extends SubCommand {

        DeleteCommand() {
            super(new String[]{"delete", "del"}, Permissions.everyone(), "Deletes a playlist", "<name>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length == 0)
                return sendHelp();

            GroovyUser groovyUser = event.getGroovyUser();
            String name = args[0];

            if (!groovyUser.getPlaylists().containsKey(name.toLowerCase()))
                return send(error(event.translate("command.playlist.not.exists.title"), event.translate("command.playlist.not.exists.description")));

            event.getBot().getPlaylistManager().deletePlaylist(name, groovyUser.getEntityId());
            return send(success(event.translate("command.playlist.deleted.title"), String.format(event.translate("command.playlist.deleted.description"), name)));
        }
    }

    private static class RenameCommand extends SubCommand {

        RenameCommand() {
            super(new String[]{"rename", "rn"}, Permissions.everyone(), "Renames a playlist", "<name> <new name>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length < 2)
                return sendHelp();

            if (args.length > 2)
                return send(error(event.translate("command.playlist.invalidname.title"), event.translate("command.playlist.invalidname.title")));

            GroovyUser groovyUser = event.getGroovyUser();
            String name = args[0].toLowerCase();
            String newName = args[1].toLowerCase();

            if (!groovyUser.getPlaylists().containsKey(name))
                return send(error(event.translate("command.playlist.not.exists.title"), event.translate("command.playlist.not.exists.description")));

            groovyUser.getPlaylists().get(name).setName(newName);
            return send(success(event.translate("command.playlist.renamed.title"), String.format(event.translate("command.playlist.renamed.description"), name, newName)));
        }
    }

    private static class AddCommand extends SemiInChannelSubCommand {

        AddCommand() {
            super(new String[]{"add"}, Permissions.everyone(), "Adds a track to a playlist", "<name> <url>");
        }

        @Override
        protected Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
            if (args.length < 2)
                return sendHelp();

            GroovyUser groovyUser = event.getGroovyUser();
            String name = args[0].toLowerCase();
            String track = args[1];

            if (!groovyUser.getPlaylists().containsKey(name))
                return send(error(event.translate("command.playlist.not.exists.title"), event.translate("command.playlist.not.exists.description")));

            if (groovyUser.getPlaylists().get(name).getSongs().size() >= 10)
                return send(error(event.translate("command.playlist.tomanysongs.title"), event.translate("command.playlist.tomanysongs.description")));

            player.getAudioPlayerManager().loadItem(track, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    groovyUser.getPlaylists().get(name).addTrack(track);
                    SafeMessage.sendMessage(event.getChannel(), success(event.translate("command.playlist.added.title"), String.format(event.translate("command.playlist.added.description"), track.getInfo().title, groovyUser.getPlaylists().get(name).getName())));
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {
                    if (audioPlaylist.getTracks().isEmpty())
                        SafeMessage.sendMessage(event.getChannel(), error(event.translate("phrases.nothingfound"), event.translate("phrases.searching.nomatches")));
                    else {
                        groovyUser.getPlaylists().get(name).addTrack(audioPlaylist.getTracks().get(0));
                        SafeMessage.sendMessage(event.getChannel(), success(event.translate("command.playlist.added.title"), String.format(event.translate("command.playlist.added.description"), audioPlaylist.getTracks().get(0).getInfo().title, groovyUser.getPlaylists().get(name).getName())));
                    }
                }

                @Override
                public void noMatches() {
                    SafeMessage.sendMessage(event.getChannel(), error(event.translate("phrases.nothingfound"), event.translate("phrases.searching.nomatches")));
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    SafeMessage.sendMessage(event.getChannel(), error(event.translate("phrases.error"), e.getCause() != null ? String.format("**%s**%n%s", e.getMessage(), e.getCause().getMessage()) : String.format("**%s**", e.getMessage())));
                }
            });
            return null;
        }
    }

    private static class RemoveCommand extends SubCommand {

        RemoveCommand() {
            super(new String[]{"remove", "rm"}, Permissions.everyone(), "Removes a song from a playlist", "<name> <position>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length < 2)
                return sendHelp();

            GroovyUser groovyUser = event.getGroovyUser();
            String name = args[0];

            if (!Helpers.isNumeric(args[1]))
                return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.number")));

            int track = Integer.parseInt(args[1]);

            if (!groovyUser.getPlaylists().containsKey(name.toLowerCase()))
                return send(error(event.translate("command.playlist.not.exists.title"), event.translate("command.playlist.not.exists.description")));

            if (groovyUser.getPlaylists().get(name).getSongs().size() < track)
                return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.number")));

            String trackName = groovyUser.getPlaylists().get(name).getSongs().get(track - 1).getInfo().title;
            String playlistName = groovyUser.getPlaylists().get(name).getName();

            groovyUser.getPlaylists().get(name).removeTrack(track - 1);
            return send(success(event.translate("command.playlist.removed.title"), String.format(event.translate("command.playlist.removed.description"), trackName, playlistName)));
        }
    }

    private static class ListCommand extends SubCommand {

        ListCommand() {
            super(new String[]{"list"}, Permissions.everyone(), "Shows all playlists of an user", "[user]");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (event.getMessage().getMentionedMembers().size() != 0) {
                GroovyUser groovyUser = event.getBot().getUserCache().get(event.getMessage().getMentionedMembers().get(0).getUser().getIdLong());

                if (groovyUser.getPlaylists().size() == 0)
                    return send(error(event.translate("command.playlist.nolist.title"), event.translate("command.playlist.nolist.description")));

                EmbedBuilder builder = new EmbedBuilder()
                        .setDescription(String.format("**%s**", String.format(event.translate("command.playlist.list.title"), event.getMessage().getMentionedMembers().get(0).getUser().getName() + "'s")))
                        .setColor(Colors.DARK_BUT_NOT_BLACK)
                        .setFooter(String.format("%s Playlists", groovyUser.getPlaylists().size()), event.getAuthor().getAvatarUrl());

                groovyUser.getPlaylists().forEach((name, groovyPlaylist) -> {
                    if (groovyPlaylist.isPublic())
                        builder.addField(String.format("%s **%s**", "\uD83D\uDD13", groovyPlaylist.getName()), String.format(" - Includes **%s** songs %n - Loaded **%s** times %n - ID: `%s`", groovyPlaylist.getSongs().size(), groovyPlaylist.getCount(), groovyPlaylist.getId()), false);
                });
                return send(builder);

            } else {
                GroovyUser groovyUser = event.getGroovyUser();

                if (groovyUser.getPlaylists().size() == 0)
                    return send(error(event.translate("command.playlist.nolist.title"), event.translate("command.playlist.nolist.description")));

                EmbedBuilder builder = new EmbedBuilder()
                        .setDescription(String.format("**%s**", String.format(event.translate("command.playlist.list.title"), "your")))
                        .setColor(Colors.DARK_BUT_NOT_BLACK)
                        .setFooter(String.format("%s Playlists", groovyUser.getPlaylists().size()), event.getAuthor().getAvatarUrl());

                groovyUser.getPlaylists().forEach((name, groovyPlaylist) -> builder.addField(String.format("%s **%s**", groovyPlaylist.isPublic() ? "\uD83D\uDD13" : "\uD83D\uDD12", groovyPlaylist.getName()), String.format(" - Includes **%s** songs %n - Loaded **%s** times %n - ID: `%s`", groovyPlaylist.getSongs().size(), groovyPlaylist.getCount(), groovyPlaylist.getId()), false));
                return send(builder);
            }
        }
    }

    private static class SongsCommand extends SubCommand {

        SongsCommand() {
            super(new String[]{"songs"}, Permissions.everyone(), "Shows all songs of a playlist", "[user] <name>/<id>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length == 0)
                return sendHelp();

            if (event.getMessage().getMentionedMembers().size() != 0) {
                if (args.length < 2)
                    return sendHelp();

                GroovyUser groovyUser = event.getBot().getUserCache().get(event.getMessage().getMentionedMembers().get(0).getUser().getIdLong());
                String name = args[1];

                if (!groovyUser.getPlaylists().containsKey(name.toLowerCase()))
                    return send(error(event.translate("command.playlist.not.exists.title"), event.translate("command.playlist.not.exists.description")));

                if (!groovyUser.getPlaylists().get(name.toLowerCase()).isPublic())
                    return send(error(event.translate("command.playlist.not.public.title"), event.translate("command.playlist.not.public.description")));

                StringBuilder tracks = new StringBuilder();
                final List<AudioTrack> songs = groovyUser.getPlaylists().get(name.toLowerCase()).getSongs();
                songs.forEach(track -> System.out.println(track.getInfo().title));
                songs.forEach(track -> tracks.append(String.format("▫ `%s.` [%s](%s) - %s", songs.indexOf(track) + 1, track.getInfo().title, track.getInfo().uri, track.getInfo().author)).append("\n"));
                return send(info(String.format(event.translate("command.playlist.songs.title"), groovyUser.getPlaylists().get(name.toLowerCase()).getName()), tracks.toString()));
            } else {
                if (Helpers.isNumeric(args[0]))
                    if (event.getBot().getPlaylistManager().getPlaylistById(Long.parseLong(args[0])) != null)
                        if (event.getBot().getPlaylistManager().getPlaylistById(Long.parseLong(args[0])).isPublic()) {
                            StringBuilder tracks = new StringBuilder();
                            final List<AudioTrack> songs = event.getBot().getPlaylistManager().getPlaylistById(Long.parseLong(args[0])).getSongs();
                            songs.forEach(track -> tracks.append(String.format("▫ `%s.` [%s](%s) - %s", songs.indexOf(track) + 1, track.getInfo().title, track.getInfo().uri, track.getInfo().author)).append("\n"));
                            return send(info(String.format(event.translate("command.playlist.songs.title"), event.getBot().getPlaylistManager().getPlaylistById(Long.parseLong(args[0])).getName()), tracks.toString()));
                        }

                GroovyUser groovyUser = event.getGroovyUser();
                String name = args[0];

                if (!groovyUser.getPlaylists().containsKey(name.toLowerCase()))
                    return send(error(event.translate("command.playlist.not.exists.title"), event.translate("command.playlist.not.exists.description")));

                StringBuilder tracks = new StringBuilder();
                final List<AudioTrack> songs = groovyUser.getPlaylists().get(name.toLowerCase()).getSongs();
                songs.forEach(track -> tracks.append(String.format("▫ `%s.` [%s](%s) - %s", songs.indexOf(track) + 1, track.getInfo().title, track.getInfo().uri, track.getInfo().author)).append("\n"));
                return send(info(String.format(event.translate("command.playlist.songs.title"), groovyUser.getPlaylists().get(name.toLowerCase()).getName()), tracks.toString()));
            }
        }
    }

    private static class ToggleVisibilityCommand extends SubCommand {

        ToggleVisibilityCommand() {
            super(new String[]{"toggle", "togglevisibility", "tv"}, Permissions.everyone(), "Toggles the visibility of a playlist", "<name>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (args.length == 0)
                return sendHelp();

            GroovyUser groovyUser = event.getGroovyUser();
            String name = args[0];

            if (groovyUser.getPlaylists().size() == 0)
                return send(error(event.translate("command.playlist.nolist.title"), event.translate("command.playlist.nolists.description")));

            if (!groovyUser.getPlaylists().containsKey(name.toLowerCase()))
                return send(error(event.translate("command.playlist.not.exists.title"), event.translate("command.playlist.not.exists.description")));

            groovyUser.getPlaylists().get(name.toLowerCase()).setPublic(!groovyUser.getPlaylists().get(name.toLowerCase()).isPublic());
            return send(success(event.translate("command.playlist.setpublic.title"), String.format(event.translate("command.playlist.setpublic.description"), groovyUser.getPlaylists().get(name.toLowerCase()).getName(), groovyUser.getPlaylists().get(name.toLowerCase()).isPublic() ? "public" : "private")));
        }
    }
}
