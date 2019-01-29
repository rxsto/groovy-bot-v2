/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.util;

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

    @PluginFactory
    public static JDAFilter createFilter() {
        return new JDAFilter();
    }

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
}
