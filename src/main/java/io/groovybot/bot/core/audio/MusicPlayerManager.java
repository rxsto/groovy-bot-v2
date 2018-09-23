package io.groovybot.bot.core.audio;


import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class MusicPlayerManager {

    private Map<Long, MusicPlayer> playerStorage = new HashMap<>();

    public MusicPlayer getPlayer(Guild guild, TextChannel channel) {
        if (playerStorage.containsKey(guild.getIdLong()))
            return playerStorage.get(guild.getIdLong());
        MusicPlayer player = new MusicPlayer(guild, channel);
        playerStorage.put(guild.getIdLong(), player);
        return player;
    }

    public void update(Guild guild, MusicPlayer player) {
        playerStorage.replace(guild.getIdLong(), player);
    }
}
