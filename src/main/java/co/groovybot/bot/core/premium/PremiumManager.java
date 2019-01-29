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

package co.groovybot.bot.core.premium;

import co.groovybot.bot.core.entity.entities.GroovyUser;

public class PremiumManager {

    /**
     * Gives a groovyUser Premium for a specified time
     *
     * @param groovyUser The groovyUser
     */
    public static void givePremium(GroovyUser groovyUser) {
        groovyUser.setVoted(System.currentTimeMillis() + Constants.PREMIUM_TIME, System.currentTimeMillis() + Constants.VOTE_AGAIN);
    }

    /**
     * Checks if a groovyUser has voted
     *
     * @param groovyUser The groovyUser that needs to be checked
     * @return if the groovyUser used the command or not
     */
    public static boolean hasVoted(GroovyUser groovyUser) {
        return groovyUser.hasVoted();
    }

    /**
     * Checks if a groovyUser has already voted
     *
     * @param groovyUser The groovyUser that needs to be checked
     * @return if the groovyUser used the command or not
     */
    public static boolean hasAlreadyVoted(GroovyUser groovyUser) {
        return groovyUser.hasAlreadyVoted();
    }

    /**
     * Checks if a groovyUser has already voted
     *
     * @param groovyUser The groovyUser that needs to be checked
     * @return if the groovyUser used the command or not
     */
    public static boolean isAbleToVote(GroovyUser groovyUser) {
        return groovyUser.isAbleToVote();
    }

    /**
     * Checks if a groovyUser has already voted
     *
     * @param groovyUser The groovyUser that needs to be checked
     * @return if the groovyUser used the command or not
     */
    public static long getVoteAgainIn(GroovyUser groovyUser) {
        return groovyUser.getAgain() - System.currentTimeMillis();
    }
}
