package co.groovybot.bot.listeners;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.util.EmbedUtil;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class JoinGuildListener {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildJoin(GuildJoinEvent event) {
        if (!checkPremium(event)) {
            event.getGuild().leave().queue();
            return;
        }
        joinMessage(event.getGuild());
    }

    private void joinMessage(Guild guild) {
        List<TextChannel> channels = guild.getTextChannels();

        Map<String, TextChannel> sortedChannels = new HashMap<>();
        Set<TextChannel> preferredChannels = new HashSet<>();

        for (TextChannel channel : channels) sortedChannels.put(channel.getName(), channel);

        sortedChannels.forEach((name, channel) -> {
            if (name.contains("music") || name.contains("bot") || name.contains("command") || name.contains("talk") || name.contains("chat") || name.contains("general"))
                preferredChannels.add(channel);
        });

        boolean found = false;

        for (TextChannel channel : preferredChannels)
            if (channel.canTalk()) {
                EmbedUtil.sendMessageBlocking(channel, EmbedUtil.welcome(guild));
                found = true;
                break;
            }

        if (!found)
            for (TextChannel channel : channels)
                if (channel.canTalk()) {
                    EmbedUtil.sendMessageBlocking(channel, EmbedUtil.welcome(guild));
                    break;
                }
    }

    private boolean checkPremium(GuildJoinEvent event) {
        AtomicBoolean checked = new AtomicBoolean(false);
        List<Long> ids = new ArrayList<>();
        try (Connection connection = GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection()) {
            PreparedStatement premium = connection.prepareStatement("SELECT user_id FROM premium");
            ResultSet premiumSet = premium.executeQuery();

            while (premiumSet.next()) {
                ids.add(premiumSet.getLong("user_id"));
            }

            PreparedStatement friend = connection.prepareStatement("SELECT user_id FROM users WHERE friend = true");
            ResultSet friendSet = friend.executeQuery();

            while (friendSet.next()) {
                ids.add(friendSet.getLong("user_id"));
            }
        } catch (SQLException e) {
            log.error("[PermissionProvider] Error while retrieving permissions!", e);
        }

        event.getGuild().getMembers().stream().filter(member -> member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR, Permission.MANAGE_SERVER)).forEach(member -> {
            if (ids.contains(member.getUser().getIdLong())) checked.set(true);
        });

        return checked.get();
    }
}
