package io.groovybot.bot.core;

import io.groovybot.bot.core.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PremiumManager {

    private final ScheduledExecutorService scheduler;
    private final List<Long> users;

    public PremiumManager() {
        this.scheduler = Executors.newScheduledThreadPool(5);
        this.users = new ArrayList<>();
    }

    /**
     * Gives a user Premium for a specified time
     *
     * @param user     The user
     * @param time     The time of the premium subscription
     * @param timeUnit The timeunit
     */
    public void givePremium(User user, long time, TimeUnit timeUnit) {
        user.setPremium(2);
        users.add(user.getEntityId());
        scheduler.schedule(() -> user.setPremium(0), time, timeUnit);
        scheduler.schedule(() -> users.remove(user.getEntityId()), 1, TimeUnit.DAYS);
    }

    /**
     * Checks if a user used that command in the last 24 hours
     *
     * @param user The user that needs to be checked
     * @return if the user used the command or not
     */
    public boolean isAllowed(User user) {
        return !users.contains(user.getEntityId());
    }
}
