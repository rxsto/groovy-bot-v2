package io.groovybot.bot.core.audio;


import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MusicPlayerManager {

    @Getter
    private Map<Long, MusicPlayer> playerStorage = new HashMap<>();
    private int playingServers = 0;

    public MusicPlayer getPlayer(Guild guild, TextChannel channel) {
        if (playerStorage.containsKey(guild.getIdLong()))
            return playerStorage.get(guild.getIdLong());
        MusicPlayer player = new MusicPlayer(guild, channel);
        playerStorage.put(guild.getIdLong(), player);
        return player;
    }

    public int getPlayingServers() {
        updatePlayingServers();
        return playingServers;
    }

    private void updatePlayingServers() {
        getPlayerStorage().forEach( (id, player) -> {
            if (player.isPlaying())
                playingServers++;
        });
    }

    public void update(Guild guild, MusicPlayer player) {
        playerStorage.replace(guild.getIdLong(), player);
    }
}
