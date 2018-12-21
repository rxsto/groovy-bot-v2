package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.Command;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;

public class PremiumCommand extends Command {

    public PremiumCommand() {
        super(new String[]{"premium", "premiumstatus", "tier"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows your current premium-status", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        if (Permissions.tierThree().isCovered(event.getPermissions(), event))
            return send(info(event.translate("command.premium.tier.title"), String.format(event.translate("command.premium.tier.description"), "Tier Three")));
        if (Permissions.tierTwo().isCovered(event.getPermissions(), event))
            return send(info(event.translate("command.premium.tier.title"), String.format(event.translate("command.premium.tier.description"), "Tier Two")));
        if (Permissions.tierOne().isCovered(event.getPermissions(), event))
            return send(info(event.translate("command.premium.tier.title"), String.format(event.translate("command.premium.tier.description"), "Tier One")));
        return send(info(event.translate("command.premium.none.title"), event.translate("command.premium.none.description")));
    }
}
