package io.groovybot.bot.io;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;
import java.time.Instant;

@Plugin(name = "WebhookAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
@SuppressWarnings("unused")
public class ErrorReporter extends AbstractAppender {

    private WebhookClient errorHook;

    public ErrorReporter(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
    }

    @PluginFactory
    public static ErrorReporter createAppender(@PluginAttribute("name") String name, @PluginElement("Filter") Filter filter) {
        return new ErrorReporter(name, filter, null);
    }

    private WebhookMessage buildErrorLog(LogEvent event) {
        WebhookMessageBuilder out = new WebhookMessageBuilder();
        Throwable throwable = event.getThrown();
        EmbedBuilder builder =  EmbedUtil.error("An unknown error occurred", String.format("An unkown error occurred in class %s", event.getLoggerName())
        )
                .addField("Class", "`" + event.getLoggerName() + "`", false)
                .addField("Message", "`" + formatException(throwable) + "`", false)
                .addField("Stacktrace", "```" + formatStacktrace(throwable) + "```", false)
                .setTimestamp(Instant.now());
        if (throwable.getCause() != null)
            builder.addField("Cause", "```" + formatStacktrace(throwable.getCause()) + "```", false);
        out.addEmbeds(builder.build());
        return out.build();
    }

    private String formatStacktrace(Throwable throwable) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i > throwable.getStackTrace().length)
                break;
            try {
                out.append(throwable.getStackTrace()[i]).append("\n");
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        return out.toString();
    }

    private String formatException(Throwable throwable) {
        return String.format("%s:%s", throwable.getClass().getCanonicalName(), throwable.getMessage());
    }

    @Override
    public void append(LogEvent event) {
        if (errorHook == null && GroovyBot.getInstance().getConfig() != null)
            errorHook = new WebhookClientBuilder(GroovyBot.getInstance().getConfig().getJSONObject("webhooks").getString("error_hook")).build();
        if (errorHook == null)
            return;
        if (event.getThrown() != null)
            errorHook.send(buildErrorLog(event));
    }
}
