package co.groovybot.bot.commands.owner;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class TestCommand extends Command {

    public TestCommand() {
        super(new String[]{"test", "t"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Testing ...", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {

        if (args.length == 0)
            return sendHelp();

        EmbedBuilder builder = new EmbedBuilder();

        Color ERROR = new Color(0xD44E4E);
        Color SUCCESS = new Color(0x5ac14b);
        Color INFO = new Color(0x386ec5);
        Color WARN = new Color(0xfac200);
        Color PLAYING = new Color(0xffffff);

        switch (args[0]) {
            case "info":
                builder
                        .setTitle("<:info:535828529573789696> Information")
                        .setDescription("This is a very informative information! This message contains much of cool stuff!")
                        .setColor(INFO);
                return send(builder);
            case "error":
                builder
                        .setTitle("<:error:535827110489620500> Error")
                        .setDescription("Ouh shit! There occurred an error! We need to fix that as soon as possible! Whoops!")
                        .setColor(ERROR);
                return send(builder);
            case "success":
                builder
                        .setTitle("<:success:535827110552666112> Success")
                        .setDescription("Yippie! It worked! You successfully have done something! That is unbelievable! Wohoooo!")
                        .setColor(SUCCESS);
                return send(builder);
            case "warn":
                builder
                        .setTitle("<:warn:535832532365737987> Warning")
                        .setDescription("This is a critical warning! You should be patient and pay attention! There will be happen something!")
                        .setColor(WARN);
                return send(builder);
            case "playing":
                builder
                        .setTitle("<:playing:535833712181510164> Now Playing")
                        .setDescription("[Epic Trap Remix - Trap Nation](https://groovybot.co)")
                        .setFooter("Requested by @Rxsto#1337 - 1m 37s", null)
                        .setColor(PLAYING);
                return send(builder);
        }

        return send("uuuf");
    }
}
