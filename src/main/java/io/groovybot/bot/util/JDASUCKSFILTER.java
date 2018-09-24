package io.groovybot.bot.util;

import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class JDASUCKSFILTER extends Filter {

    @Override
    public int decide(LoggingEvent event) {
        if (event.getThrowableInformation() != null && event.getThrowableInformation().getThrowable() instanceof ErrorResponseException)
            return DENY;
        return ACCEPT;
    }
}
