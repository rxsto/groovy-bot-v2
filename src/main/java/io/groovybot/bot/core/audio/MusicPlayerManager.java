package io.groovybot.bot.core.audio;


import io.groovybot.bot.GroovyBot;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class MusicPlayerManager {

    @Getter
    private Map<Long, MusicPlayer> playerStorage = new HashMap<>();

    public MusicPlayer getPlayer(Guild guild, TextChannel channel) {
        if (playerStorage.containsKey(guild.getIdLong()))
            return playerStorage.get(guild.getIdLong());
        MusicPlayer player = new MusicPlayer(guild, channel, GroovyBot.getInstance().getYoutubeClient());
        playerStorage.put(guild.getIdLong(), player);
        return player;
    }

    public int getPlayingServers() {
        return GroovyBot.getInstance().getLavalinkManager().countPlayers();
    }

    public void update(Guild guild, MusicPlayer player) {
        playerStorage.replace(guild.getIdLong(), player);
    }
}
