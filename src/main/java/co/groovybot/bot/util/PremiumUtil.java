package co.groovybot.bot.util;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.premium.Tier;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;

public class PremiumUtil {

    public static Tier getTier(Member member, Guild guild) {
        if (member.getRoles().contains(guild.getRoleById(1234L)))
            return Tier.ONE;
        if (member.getRoles().contains(guild.getRoleById(5678L)))
            return Tier.TWO;
        if (member.getRoles().contains(guild.getRoleById(9101L)))
            return Tier.THREE;
        return Tier.NONE;
    }

    public static boolean hasPremiumRole(List<Role> roles) {
        return roles.contains(GroovyBot.getInstance().getSupportGuild().getRoleById(1234L)) || roles.contains(GroovyBot.getInstance().getSupportGuild().getRoleById(1234L)) || roles.contains(GroovyBot.getInstance().getSupportGuild().getRoleById(1234L));
    }
}
