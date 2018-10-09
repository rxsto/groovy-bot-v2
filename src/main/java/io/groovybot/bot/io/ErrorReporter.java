package io.groovybot.bot.io;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.util.EmbedUtil;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import java.io.Serializable;
import java.time.Instant;

public class ErrorReporter extends AbstractAppender {

    private final WebhookClient errorHook;

    public ErrorReporter(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
        errorHook = new WebhookClientBuilder(GroovyBot.getInstance().getConfig().getJSONObject("webhooks").getString("error_hook")).build();
    }


    private WebhookMessage buildErrorLog(LogEvent event) {
        WebhookMessageBuilder out = new WebhookMessageBuilder();
        out.addEmbeds(
                EmbedUtil.error("An unknown error occurred", String.format("An unkown error occurred in class %s", event.getLoggerName())
                )
                        .addField("Class", "`" + event.getLoggerName() + "`", false)
                        .addField("Message", "`" + formatException(event.getThrown()) + "`", false)
                        .addField("Stacktrace", "```" + formatStacktrace(event.getThrown()) + "```", false)
                        .setTimestamp(Instant.now())
                        .build()
        );
        return out.build();
    }

    private String formatStacktrace(Throwable throwable) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            out.append(throwable.getStackTrace()[i]).append("\n");
        }
        return out.toString();
    }

    private String formatException(Throwable throwable) {
        return String.format("%s:%s", throwable.getClass().getCanonicalName(), throwable.getMessage());
    }

    @Override
    public void append(LogEvent event) {
        if (errorHook == null)
            return;
        if (event.getThrown() != null)
            errorHook.send(buildErrorLog(event));
    }
}
