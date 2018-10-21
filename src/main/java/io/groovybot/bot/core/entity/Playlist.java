package io.groovybot.bot.core.entity;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.GroovyBot;
import lavalink.client.LavalinkUtil;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@ToString
@Getter
public class Playlist {

    private int id;
    private String name;
    private Long ownerId;
    private List<AudioTrack> songs;

    public Playlist(ResultSet rs) {
        try {
            this.ownerId = rs.getLong("owner_id");
            this.songs = decodeTracks(new JSONArray(rs.getString("tracks")));
            this.name = rs.getString("name");
            this.id = rs.getInt("id");
        } catch (SQLException e) {
            log.error("[Playlist] Error while retrieving playlist", e);
        }
    }

    public Playlist(String name, Long ownerId, List<AudioTrack> songs) {
        this.name = name;
        this.ownerId = ownerId;
        this.songs = songs;
        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO playlists (owner_id, tracks, name) VALUES (?, ?, ?)");
            ps.setLong(1, ownerId);
            ps.setString(2, convertTracks().toString());
            ps.setString(3, name);
            ps.execute();
        } catch (SQLException e) {
            log.error("[Playlist] Error while saving playlist", e);
        }
    }

    private JSONArray convertTracks() {
        JSONArray tracks = new JSONArray();
        songs.forEach(track -> {
            try {
                tracks.put(LavalinkUtil.toMessage(track));
            } catch (IOException e) {
                log.warn("[Playlist] Error while decoding song", e);
            }
        });
        return tracks;
    }

    private void update() {
        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE playlists SET tracks = ? WHERE id = ?"
            );
            ps.setString(1, convertTracks().toString());
            ps.setInt(2, id);
            ps.execute();
        } catch (SQLException e) {
            log.error("[Playlist] Error while saving playlist", e);
        }
    }

    public void addTrack(AudioTrack track) {
        songs.add(track);
        update();
    }

    public void removeTrack(int index) {
        songs.remove(index);
        update();
    }

    private List<AudioTrack> decodeTracks(JSONArray identifiers) {
        List<AudioTrack> out = new ArrayList<>();
        for (Object identifier : identifiers) {
            try {
                out.add(LavalinkUtil.toAudioTrack((String) identifier));
            } catch (IOException e) {
                log.warn("[Playlist] Error while decoding song", e);
            }
        }
        return out;
    }
}
