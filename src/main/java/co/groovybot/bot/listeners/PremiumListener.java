package co.groovybot.bot.listeners;

import co.groovybot.bot.core.premium.PremiumHandler;
import co.groovybot.bot.core.premium.Tier;
import co.groovybot.bot.util.PremiumUtil;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class PremiumListener {

    private final PremiumHandler premiumHandler;

    public PremiumListener(PremiumHandler premiumHandler) {
        this.premiumHandler = premiumHandler;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onRoleAdd(GuildMemberRoleAddEvent event) {
        if (event.getGuild().getIdLong() != 403882830225997825L) return;
        Tier tier = PremiumUtil.getTier(event.getMember(), event.getGuild());
        if (tier == Tier.NONE) return;
        premiumHandler.addPatron(event.getUser().getIdLong(), tier);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onRoleRemove(GuildMemberRoleRemoveEvent event) {
        if (event.getGuild().getIdLong() != 403882830225997825L) return;
        if (PremiumUtil.hasPremiumRole(event.getRoles()))
            premiumHandler.removePatron(event.getUser().getIdLong());
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onLeave(GuildMemberLeaveEvent event) {
        if (event.getGuild().getIdLong() != 403882830225997825L) return;
        if (PremiumUtil.hasPremiumRole(event.getMember().getRoles()))
            premiumHandler.removePatron(event.getUser().getIdLong());
    }
}
