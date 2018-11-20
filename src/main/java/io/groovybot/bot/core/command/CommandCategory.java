package io.groovybot.bot.core.command;

import lombok.Getter;

@Getter
public enum CommandCategory {

    PREMIUM("Premium"),
    MUSIC("Music"),
    DEVELOPER("Developer"),
    SETTINGS("Settings"),
    GENERAL("General");

    private String displayName;

    CommandCategory(String displayName) {
        this.displayName = displayName;
    }
}
