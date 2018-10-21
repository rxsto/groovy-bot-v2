package io.groovybot.bot.util;

import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;

@Plugin(name = "JDAFilter", category = Core.CATEGORY_NAME, elementType = Filter.ELEMENT_TYPE)
@SuppressWarnings("unused")
public class JDAFilter extends AbstractFilter {

    @Override
    public Result filter(LogEvent event) {
        return decide(event);
    }

    public Result decide(LogEvent event) {
        if (event.getMessage().getFormattedMessage().contains("org.apache.http.wire"))
            return Result.DENY;
        if (event.getThrown() != null && event.getThrown() instanceof ErrorResponseException)
            return Result.DENY;
        return Result.ACCEPT;
    }

    @PluginFactory
    public static JDAFilter createFilter() {
        return new JDAFilter();
    }
}
