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

package co.groovybot.bot.commands.owner;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import lombok.extern.log4j.Log4j2;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Log4j2
public class EvalCommand extends Command {

    public EvalCommand() {
        super(new String[]{"eval"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Run code with Groovy", "<code>");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0) {
            return sendHelp();
        }

        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("Nashorn");

        try {
            scriptEngine.eval("var imports = new JavaImporter(" +
                    "java.nio.file," +
                    "Packages.net.dv8tion.jda.core," +
                    "java.lang," +
                    "java.lang.management," +
                    "java.text," +
                    "java.sql," +
                    "java.util," +
                    "java.time," +
                    "Packages.com.sun.management" +
                    ");");
        } catch (ScriptException e) {
            log.error("[EvalCommand] Error while importing libs!", e);
            return send(error(event));
        }

        scriptEngine.put("bot", event.getBot());
        scriptEngine.put("jda", event.getJDA());
        scriptEngine.put("guild", event.getGuild());
        scriptEngine.put("channel", event.getChannel());
        scriptEngine.put("message", event.getMessage());
        scriptEngine.put("author", event.getAuthor());

        String code = event.getArguments();

        if (code.toLowerCase().contains("token"))
            return send(error("Oh no", "You wanted to leak our token"));

        try {
            Object result = scriptEngine.eval("{" + "with (imports) {" + code + "}" + "};");
            return send(success("Evaluated successfully", String.format("```%s```", result.toString())));
        } catch (ScriptException e) {
            return send(error("An error occurred!", String.format("An exception was thrown: ```%s```", e.getMessage())));
        }
    }
}
