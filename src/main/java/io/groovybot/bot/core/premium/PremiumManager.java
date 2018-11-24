package io.groovybot.bot.core.premium;

import io.groovybot.bot.core.entity.User;

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
}
