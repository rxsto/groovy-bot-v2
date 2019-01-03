package co.groovybot.bot.core.logging;

import co.groovybot.bot.GroovyBot;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.graylog2.gelfclient.GelfMessageBuilder;
import org.graylog2.gelfclient.GelfMessageLevel;

import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("unused")
@Plugin(name = "Graylog", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class GraylogAppender extends AbstractAppender {

    public GraylogAppender(String name, Filter filter) {
        super(name, filter, null);
    }

    @PluginFactory
    public static GraylogAppender createAppender(@PluginAttribute("name") String name, @PluginElement("Filter") Filter filter) {
        return new GraylogAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        if (GroovyBot.getInstance().getGelfTransport() == null)
            return;
        GelfMessageBuilder builder = new
                GelfMessageBuilder(event.getMessage().getFormattedMessage());
        if (event.getThrown() != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            event.getThrown().printStackTrace(printWriter);
            builder.fullMessage(stringWriter.toString());
        }

        builder.level(GelfMessageLevel.valueOf(event.getLevel().name()));
        if (event.getSource() != null)
            builder.additionalField("manager", event.getSource().toString());
        builder.additionalField("instance", GroovyBot.getInstance().getInstanceName());
        builder.additionalField("level_name", event.getLevel().name());
        try {
            GroovyBot.getInstance().getGelfTransport().send(builder.build());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
