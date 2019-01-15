package co.groovybot.bot.core.logging;

import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

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
            Sentry.getContext().addExtra("message", event.getMessage().getFormattedMessage());
            Sentry.getContext().addExtra("level", event.getLevel().name());
            Sentry.getContext().addExtra("thread-info", String.format("%s (%s)", event.getThreadName(), event.getThreadId()));

            if (event.getSource() != null)
                Sentry.getContext().addExtra("source", event.getSource().toString());

            if (event.getThrown() != null)
                Sentry.capture(event.getThrown());
            else
                Sentry.capture(event.getMessage().getFormattedMessage());

            log.info("Successfully posted LogEvent to Sentry!");

            Sentry.getContext().clear();
        }
    }
}
