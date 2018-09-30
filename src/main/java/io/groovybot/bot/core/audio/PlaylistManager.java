package io.groovybot.bot.core.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.core.entity.Playlist;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
@RequiredArgsConstructor
public class PlaylistManager {

    private final Connection connection;

    public Playlist createPlaylist(String name, Long ownerId, List<AudioTrack> tracks) {
        return new Playlist(name, ownerId, tracks);
    }

    public Map<String, Playlist> getPlaylist(Long ownerId) {
        Map<String, Playlist> out = new HashMap<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM playlists WHERE owner_id = ?");
            ps.setLong(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Playlist playlist = new Playlist(rs);
                out.put(playlist.getName().toLowerCase(), playlist);
            }
        } catch (SQLException e) {
            log.error("[Playlist] Error while retrieving playlist", e);
        }
        return out;
    }

    public void deletePlaylist(String name, Long ownerId) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM playlists WHERE owner_id = ? AND name = ?");
            ps.setLong(1, ownerId);
            ps.setString(2, name);
            ps.execute();
        } catch (SQLException e) {
            log.error("[Playlist] Error while deleting playlist", e);
        }
    }

}
