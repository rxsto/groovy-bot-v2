package co.groovybot.bot.core.logging;

import io.sentry.Sentry;
import io.sentry.event.Event;
import io.sentry.event.EventBuilder;
import io.sentry.event.interfaces.ExceptionInterface;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.util.Date;

@SuppressWarnings("unused")
@Log4j2
@Plugin(name = "Sentry", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class SentryAppender extends AbstractAppender {

    public SentryAppender(String name, Filter filter) {
        super(name, filter, null);
    }

    @PluginFactory
    public static SentryAppender createAppender(@PluginAttribute("name") String name, @PluginElement("Filter") Filter filter) {
        return new SentryAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        if (event.getLevel().name().equalsIgnoreCase("error") || event.getLevel().name().equalsIgnoreCase("warn") || event.getLevel().name().equalsIgnoreCase("fatal")) {
            EventBuilder eventBuilder = new EventBuilder()
                    .withTimestamp(new Date())
                    .withMessage(event.getMessage().getFormattedMessage())
                    .withExtra("level", event.getLevel().name())
                    .withExtra("thread-info", String.format("%s (%s)", event.getThreadName(), event.getThreadId()));

            if (event.getLevel().equals(Level.WARN))
                eventBuilder.withLevel(Event.Level.WARNING);
            else
                eventBuilder.withLevel(Event.Level.valueOf(event.getLevel().name()));

            if (event.getSource() != null)
                eventBuilder.withLogger(event.getSource().getClassName());

            if (event.getThrown() != null)
                eventBuilder.withSentryInterface(new ExceptionInterface(event.getThrown()));

            Sentry.capture(eventBuilder);
        }
    }
}
