package io.groovybot.bot.util;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.requests.RequestFuture;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.concurrent.ExecutionException;

@Log4j2
public class JDAUtil {

    /**
     * Waits for an JDA entity without blocking the Thread
     * @param action The RestAction
     * @param <T> The return type
     * @return T The RestActions output
     */
    public static <T> T waitForEntity(RestAction<T> action) {
        RequestFuture<T> future = action.submit();
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("[Message] Could not wait for entity", e);
            return future.getNow(null);
        }
    }
}
