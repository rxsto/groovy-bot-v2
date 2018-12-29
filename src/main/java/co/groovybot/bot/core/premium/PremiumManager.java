/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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

package co.groovybot.bot.core.premium;

import co.groovybot.bot.core.entity.User;

public class PremiumManager {

    /**
     * Gives a user Premium for a specified time
     *
     * @param user The user
     */
    public static void givePremium(User user) {
        user.setVoted(System.currentTimeMillis() + Constants.PREMIUM_TIME, System.currentTimeMillis() + Constants.VOTE_AGAIN);
    }

    /**
     * Checks if a user has voted
     *
     * @param user The user that needs to be checked
     * @return if the user used the command or not
     */
    public static boolean hasVoted(User user) {
        return user.hasVoted();
    }

    /**
     * Checks if a user has already voted
     *
     * @param user The user that needs to be checked
     * @return if the user used the command or not
     */
    public static boolean hasAlreadyVoted(User user) {
        return user.hasAlreadyVoted();
    }

    /**
     * Checks if a user has already voted
     *
     * @param user The user that needs to be checked
     * @return if the user used the command or not
     */
    public static boolean isAbleToVote(User user) {
        return user.isAbleToVote();
    }

    /**
     * Checks if a user has already voted
     *
     * @param user The user that needs to be checked
     * @return if the user used the command or not
     */
    public static long getVoteAgainIn(User user) {
        return user.getAgain() - System.currentTimeMillis();
    }
}
