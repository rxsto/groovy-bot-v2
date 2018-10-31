package io.groovybot.bot.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import io.groovybot.bot.core.audio.QueuedTrack;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.SubCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.groovybot.bot.util.EmbedUtil.info;

public class FormatUtil {

    public static String formatTrack(AudioTrack track) {
        QueuedTrack queuedTrack = ((QueuedTrack) track);
        AudioTrackInfo trackInfo = queuedTrack.getInfo();
        return String.format("%s (%s) **Requested by %s**", trackInfo.title, trackInfo.author, formatUserName(queuedTrack.getRequester()));
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
        stringBuilder.append("Command aliases: `").append(Arrays.toString(command.getAliases()).replace("[", "").replace("]", "")).append("`\n");
        stringBuilder.append("Description: `").append(command.getDescription()).append("`").append("\n");
        stringBuilder.append("Usage: `").append(buildUsage(command)).append("`\n");
        command.getSubCommandAssociations().values().parallelStream().distinct().collect(Collectors.toList()).forEach(subCommand -> stringBuilder.append(buildUsage(subCommand)).append("\n"));
        return stringBuilder;
    }

    private static String buildUsage(Command command) {
        if (command instanceof SubCommand) {
            SubCommand subCommand = ((SubCommand) command);
            return "g!" + subCommand.getMainCommand().getName() + " " + subCommand.getName() + " " + subCommand.getUsage() + " - " + subCommand.getDescription();
        }
        return "g!" + command.getName() + " " + command.getUsage();
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
}
