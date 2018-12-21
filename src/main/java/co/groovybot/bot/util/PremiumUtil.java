package co.groovybot.bot.util;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.premium.Tier;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;

public class PremiumUtil {

    public static Tier getTier(Member member, Guild guild) {
        if (member.getRoles().contains(guild.getRoleById(525727475364265985L)))
            return Tier.ONE;
        if (member.getRoles().contains(guild.getRoleById(525727525037408260L)))
            return Tier.TWO;
        if (member.getRoles().contains(guild.getRoleById(525727573301526538L)))
            return Tier.THREE;
        return Tier.NONE;
    }

    public static boolean hasPremiumRole(List<Role> roles) {
        return roles.contains(GroovyBot.getInstance().getSupportGuild().getRoleById(525727475364265985L)) || roles.contains(GroovyBot.getInstance().getSupportGuild().getRoleById(525727525037408260L)) || roles.contains(GroovyBot.getInstance().getSupportGuild().getRoleById(525727573301526538L));
    }
}
