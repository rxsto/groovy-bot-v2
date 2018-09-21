package io.groovybot.bot.core.command;

import lombok.Getter;

@Getter
public enum CommandCategory {

    PREMIUM_OWNLY("Premium only"),
    MUSIC("Music"),
    ADMIN("Admin"),
    SETTINGS("Settings"),
    GENERAL("General");

    private String displayName;

    CommandCategory(String displayName) {
        this.displayName = displayName;
    }
}
