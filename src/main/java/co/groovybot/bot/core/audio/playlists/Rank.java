package co.groovybot.bot.core.audio.playlists;

import lombok.Getter;

@Getter
public enum Rank {
    ONE(":one:"),
    TWO(":two:"),
    THREE(":three:"),
    FOUR(":four:"),
    FIVE(":five:");

    private String name;

    Rank(String name) {
        this.name = name;
    }
}
