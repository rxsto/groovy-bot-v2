/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.util;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.requests.RequestFuture;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.concurrent.ExecutionException;

@Log4j2
public class JDAUtil {

    /**
     * Waits for an JDA entity without blocking the Thread
     *
     * @param action The RestAction
     * @param <T>    The return type
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
