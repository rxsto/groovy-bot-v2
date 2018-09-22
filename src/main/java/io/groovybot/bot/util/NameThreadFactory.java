package io.groovybot.bot.util;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

@RequiredArgsConstructor
public class NameThreadFactory implements ThreadFactory {

    private final String name;

    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new Thread(r, name);
    }
}
