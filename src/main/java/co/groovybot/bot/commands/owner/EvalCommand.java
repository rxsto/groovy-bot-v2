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

        if (code.toLowerCase().contains("gettoken"))
            return send(error("Oh no", "You wanted to leak our token"));

        try {
            Object result = scriptEngine.eval(
                    "{" +
                            "with (imports) {" +
                            code +
                            "}" +
                            "};");
            return send(info("Evaluated successfully", String.format("```%s```", result.toString())));
        } catch (ScriptException e) {
            return send(error("An error occurred!", String.format("An exception was thrown: ```%s```", e.getMessage())));
        }
    }
}
