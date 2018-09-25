package io.groovybot.bot.io;

import io.groovybot.bot.GroovyBot;
import io.groovybot.bot.util.EmbedUtil;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.time.Instant;

public class ErrorReporter extends AppenderSkeleton {

    private final WebhookClient errorHook;

    public ErrorReporter() {
        errorHook = new WebhookClientBuilder(GroovyBot.getInstance().getConfig().getJSONObject("webhooks").getString("error_hook")).build();
    }

    @Override
    protected void append(LoggingEvent event) {
        if (errorHook == null)
            return;
        if (event.getThrowableInformation() != null)
            errorHook.send(buildErrorLog(event));
    }

    @Override
    public void close() {
        errorHook.close();
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    private WebhookMessage buildErrorLog(LoggingEvent event) {
        WebhookMessageBuilder out = new WebhookMessageBuilder();
        out.addEmbeds(
                EmbedUtil.error("An unknown error occurred", String.format("An unkown error occurred in class %s", event.getLoggerName())
                )
                        .addField("Class", "`" + event.getLoggerName() + "`", false)
                        .addField("Message", "`" + formatException(event.getThrowableInformation().getThrowable()) + "`", false)
                        .addField("Stacktrace", "```" + formatStacktrace(event.getThrowableInformation().getThrowable()) + "```", false)
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
}
