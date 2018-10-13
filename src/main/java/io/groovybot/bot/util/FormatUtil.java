package io.groovybot.bot.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.groovybot.bot.core.command.Command;
import io.groovybot.bot.core.command.SubCommand;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

import static io.groovybot.bot.util.EmbedUtil.info;

public class FormatUtil {

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
     * @return the timespamt as a string
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

}
