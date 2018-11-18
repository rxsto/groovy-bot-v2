package io.groovybot.bot.core.audio.spotify.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserPlaylistKey {

    private final String userId, playlistId;
}
