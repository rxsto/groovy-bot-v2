package io.groovybot.bot.util;

import com.google.common.collect.Lists;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.CommandEvent;
import io.groovybot.bot.core.command.SubCommand;
import io.groovybot.bot.core.events.command.CommandFailEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.groovybot.bot.util.EmbedUtil.info;

public class FormatUtil {

    public static String formatTrack(AudioTrack audioTrack) {
        return String.format("%s (%s)", audioTrack.getInfo().title, audioTrack.getInfo().author);
    }

    /**
     * Retrieves the thumbnail of a Youtube video
     *
     * @param track The AudioTrack {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack} of the video
     * @return The thumbnails URL
     */
    public static String getThumbnail(AudioTrack track) {
        return String.format("https://img.youtube.com/vi/%s/default.jpg", track.getIdentifier());
    }

    /**
     * Formats the milliseconds of a song duration to a readable timestamp
     *
     * @param millis The milliseconds
     * @return the timestamp as a string
     */
    public static String formatTimestamp(long millis) {
        long seconds = millis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    /**
     * Formats the helpmessage for a command
     *
     * @param command The command
     * @return an EmbedBuilder
     */
    public static EmbedBuilder formatCommand(Command command) {
        return info(command.getName() + " - Help", formatUsage(command));
    }

    private static String formatUsage(Command command) {
        StringBuilder stringBuilder = new StringBuilder();
        return addUsages(stringBuilder, command).toString();
    }

    private static StringBuilder addUsages(StringBuilder stringBuilder, Command command) {
        stringBuilder.append("Usage: `").append(buildUsage(command)).append("`\n");
        stringBuilder.append("Aliases: `").append(Arrays.toString(command.getAliases()).replace("[", "").replace("]", "")).append("`\n");
        stringBuilder.append("Description: `").append(command.getDescription()).append("`").append("\n");

        if (!command.getSubCommandAssociations().isEmpty()) {
            stringBuilder.append("Subcommands:").append("\n");
            command.getSubCommandAssociations().values().parallelStream().distinct().collect(Collectors.toList()).forEach(subCommand -> stringBuilder.append(buildUsage(subCommand)).append("\n"));
        }

        return stringBuilder;
    }

    private static String buildUsage(Command command) {
        if (command instanceof SubCommand) {
            SubCommand subCommand = ((SubCommand) command);
            return String.format("▫ `g!%s %s %s` `%s`", subCommand.getMainCommand().getName(), subCommand.getName(), subCommand.getUsage(), subCommand.getDescription());
        }
        return String.format("g!%s %s", command.getName(), command.getUsage());
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + "B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + ("");
        return String.format("%.1f%sB", bytes / Math.pow(unit, exp), pre).replace(",", ".");
    }

    public static String parseUptime(long time) {
        int days = (int) (time / 24 / 60 / 60 / 1000);
        int hours = (int) ((time - days * 86400000) / 60 / 60 / 1000);
        int mins = (int) ((time - days * 86400000 - hours * 3600000) / 60 / 1000);
        int secs = (int) ((time - days * 86400000 - hours * 3600000 - mins * 60000) / 1000);
        return String.format("%sd, %sh, %smin, %ss", days, hours, mins, secs);
    }

    public static String formatUserName(User user) {
        return String.format("%s#%s", user.getName(), user.getDiscriminator());
    }

    /**
     * Converts a timestamp like 2:34 into it's millis
     *
     * @param timestamp The timestamp
     * @return The timestamp's millis
     * @throws ParseException when the provided String where invalid
     */
    public static long convertTimestamp(String timestamp) throws ParseException {
        DateFormat dateFormat = null;
        int count = timestamp.split(":").length;
        if (count == 1)
            dateFormat = new SimpleDateFormat("ss");
        else if (count == 2)
            dateFormat = new SimpleDateFormat("mm:ss");
        else if (count > 2)
            dateFormat = new SimpleDateFormat("HH:mm:ss");
        assert dateFormat != null;
        return dateFormat.parse(timestamp).getTime() + TimeUnit.HOURS.toMillis(1);
    }

    public static String formatLyrics(String lyrics) {
        String edit = lyrics.replaceAll("\\[(.*?)]", "**[$1]**");
        if (edit.length() > 2000) {
            String pre = edit.substring(0, 2000);
            if (pre.substring(pre.lastIndexOf(' ') + 1).contains("**"))
                pre = pre.substring(0, pre.lastIndexOf(" "));
            return pre + " ...";
        }
        return edit;
    }

    public static EmbedBuilder parseShardsMessage(CommandEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder stringBuilder = new StringBuilder();

        Lists.reverse(event.getBot().getShardManager().getShards()).forEach(shard -> {
            stringBuilder.append(String.format("**%s Shard %s %s » %sms**", event.getBot().getShardManager().getStatus(shard.getShardInfo().getShardId()).toString().equals("CONNECTED") ? "<:online:449207830105554964>" : "<:dnd:449207827660013568>", shard.getShardInfo().getShardId() + 1, event.getBot().getShardManager().getStatus(shard.getShardInfo().getShardId()).toString().equals("CONNECTED") ? "online" : event.getBot().getShardManager().getStatus(shard.getShardInfo().getShardId()).toString(), shard.getPing()));
            if (event.getBot().getShardManager().getShardsTotal() > 1) stringBuilder.append("\n");
        });

        embedBuilder.setDescription(stringBuilder.toString());
        embedBuilder.setColor(Colors.DARK_BUT_NOT_BLACK);
        return embedBuilder;
    }

    public static EmbedBuilder formatWebhookMessage(String type, Event event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setFooter(event.getJDA().getSelfUser().getName(), event.getJDA().getSelfUser().getAvatarUrl());
        embedBuilder.setTimestamp(Instant.now());

        switch (type) {
            case "GUILDJOIN":
                GuildJoinEvent guildJoinEvent = ((GuildJoinEvent) event);
                embedBuilder.setTitle(String.format("✅ Joined Guild %s", guildJoinEvent.getGuild().getName()));
                embedBuilder.setDescription(formatGuildLog(((GuildJoinEvent) event).getGuild()));
                embedBuilder.setThumbnail(((GuildJoinEvent) event).getGuild().getIconUrl());
                break;
            case "GUILDLEAVE":
                GuildLeaveEvent guildLeaveEvent = ((GuildLeaveEvent) event);
                embedBuilder.setTitle(String.format("❌ Left Guild %s", guildLeaveEvent.getGuild().getName()));
                embedBuilder.setDescription(formatGuildLog(((GuildLeaveEvent) event).getGuild()));
                embedBuilder.setThumbnail(((GuildLeaveEvent) event).getGuild().getIconUrl());
                break;
            case "MEMBERJOIN":
                GuildMemberJoinEvent guildMemberJoinEvent = ((GuildMemberJoinEvent) event);
                embedBuilder.setTitle(String.format("✅ User %s Joined", formatUserName(guildMemberJoinEvent.getUser())));
                embedBuilder.setDescription(formatMemberLog(((GuildMemberJoinEvent) event).getMember()));
                embedBuilder.setThumbnail(((GuildMemberJoinEvent) event).getMember().getUser().getAvatarUrl());
                break;
            case "MEMBERLEAVE":
                GuildMemberLeaveEvent guildMemberLeaveEvent = ((GuildMemberLeaveEvent) event);
                embedBuilder.setTitle(String.format("❌ User %s Left", formatUserName(guildMemberLeaveEvent.getUser())));
                embedBuilder.setDescription(formatMemberLog(((GuildMemberLeaveEvent) event).getMember()));
                embedBuilder.setThumbnail(((GuildMemberLeaveEvent) event).getMember().getUser().getAvatarUrl());
                break;
            case "ERROR":
                CommandFailEvent commandFailEvent = ((CommandFailEvent) event);
                embedBuilder.setTitle(String.format("⚠ Command '%s' Failed", commandFailEvent.getCommand().getAliases()[0]));
                embedBuilder.addField("Message", "`" + formatException(commandFailEvent.getThrowable()) + "`", false);
                embedBuilder.addField("Stacktrace", "```" + formatStacktrace(commandFailEvent.getThrowable()) + "```", false);
                break;
        }

        return embedBuilder;
    }

    public static String formatStacktrace(Throwable throwable) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i > throwable.getStackTrace().length)
                break;
            try {
                out.append(throwable.getStackTrace()[i]).append("\n");
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        return out.toString();
    }

    public static String formatException(Throwable throwable) {
        return String.format("%s:%s", throwable.getClass().getCanonicalName(), throwable.getMessage());
    }

    public static String formatGuildLog(Guild guild) {
        return String.format("**Owner:** %s\n**Created:** %s\n**Members:** `%s`\n**Identifier:** `%s`", guild.getOwner().getAsMention(), guild.getCreationTime().format(DateTimeFormatter.ISO_LOCAL_DATE).replaceAll("-", "."), guild.getMembers().size(), guild.getId());
    }

    public static String formatMemberLog(Member member) {
        return String.format("**Mention:** %s\n**Joined:** %s\n**Count:** `#%s`\n**Identifier:** `%s`", member.getAsMention(), member.getJoinDate().format(DateTimeFormatter.ISO_LOCAL_DATE).replaceAll("-", "."), member.getGuild().getMembers().size(), member.getUser().getId());
    }
}
