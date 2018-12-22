package co.groovybot.bot.listeners;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.command.permission.UserPermissions;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class PremiumExecutor {

    private final GroovyBot bot;

    @SubscribeEvent
    private void handleJoin(GuildJoinEvent event) {
        if (!new UserPermissions(bot.getUserCache().get(event.getGuild().getOwner().getUser().getIdLong()), bot).isAbleToInvite())
            event.getGuild().leave().queue();
    }
}
