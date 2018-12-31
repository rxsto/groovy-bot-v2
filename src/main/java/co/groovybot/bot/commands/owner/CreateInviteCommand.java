package co.groovybot.bot.commands.owner;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public class CreateInviteCommand extends Command {

    public CreateInviteCommand() {
        super(new String[]{"createinvite", "ci"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Lets you create invites", "<ID>");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (args.length == 0)
            return sendHelp();

        long id;

        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.id")));
        }

        Guild guild = event.getBot().getShardManager().getGuildById(id);

        if (guild == null)
            return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.id")));

        TextChannel textChannel = guild.getTextChannels().stream().filter(channel -> guild.getSelfMember().hasPermission(channel, Permission.CREATE_INSTANT_INVITE)).findFirst().orElse(null);

        if (textChannel == null)
            return send(error(event.translate("phrases.error"), event.translate("phrases.nopermission")));

        return send(small(String.format("%s", textChannel.createInvite().setMaxAge(0).setMaxUses(0).complete().getURL())));
    }
}
