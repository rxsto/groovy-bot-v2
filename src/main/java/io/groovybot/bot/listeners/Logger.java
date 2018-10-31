package io.groovybot.bot.listeners;

import io.groovybot.bot.core.events.command.CommandExecutedEvent;
import io.groovybot.bot.core.events.command.CommandFailEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class Logger {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onUserJoin(GuildMemberJoinEvent event) {
        if (!event.getGuild().getId().equals("403882830225997825"))
            return;
        sendLog();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onUserLeave(GuildMemberLeaveEvent event) {
        if (!event.getGuild().getId().equals("403882830225997825"))
            return;
        sendLog();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildJoin(GuildJoinEvent event) {
        sendLog();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onGuildLeave(GuildLeaveEvent event) {
        sendLog();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onCommandExecution(CommandExecutedEvent executedEvent) {
        sendLog();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onCommandFail(CommandFailEvent failEvent) {
        sendLog();
    }

    private void sendLog() {

    }
}
