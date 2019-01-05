package co.groovybot.bot.commands.music;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandCategory;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.Result;
import co.groovybot.bot.core.command.permission.Permissions;
import co.groovybot.bot.core.command.voice.SemiInChannelCommand;
import co.groovybot.bot.util.SafeMessage;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class TextToSpeechCommand extends SemiInChannelCommand {

    private final Options options;

    public TextToSpeechCommand() {
        super(new String[]{"tts"}, CommandCategory.GENERAL, Permissions.tierOne(), "Lets you convert text to speech", "[-S <speed>] [-L <language>] -T text");
        options = new Options();
        options.addRequiredOption("T", "text", true, "Sets the text");
        options.addOption("S", "speed", true, "Sets the speed");
        options.addOption("L", "language", true, "Sets the language");
    }

    @Override
    protected Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0)
            return sendHelp();

        CommandLine cmd;

        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            log.error(e);
            return send(error(event.translate("phrases.error"), String.format("```%s```", e.getMessage())));
        }

        String text = String.join(" ", cmd.getOptionValues("text")) + " " + String.join(" ", cmd.getArgs());
        OptionalInt speed = cmd.hasOption("speed") ? getNumberFromString(cmd.getOptionValue("speed")) : OptionalInt.of(0);

        if (!speed.isPresent() || speed.getAsInt() > 10 || speed.getAsInt() < -10)
            return send(error(event.translate("phrases.invalid"), event.translate("phrases.invalid.number")));

        Language language = cmd.hasOption("language") ? Language.fromCode(cmd.getOptionValue("language").toLowerCase()) : Language.EN_US;

        if (language == Language.UNKNOWN)
            return send(error(event.translate("phrases.invalid"), String.format(event.translate("phrases.invalid.language"),
                    Stream.of(Language.values()).filter(l -> l != Language.UNKNOWN).map(l -> "`" + l.code + "`").collect(Collectors.joining(", ")))));

        String url;
        try {
            url = buildUrl(text, language, speed.getAsInt());
        } catch (UnsupportedEncodingException e) {
            log.error(e);
            return send(error(event.translate("phrases.error"), event.translate("phrases.error.unknown")));
        }

        Message infoMessage = SafeMessage.sendMessageBlocking(event.getChannel(), small(event.translate("phrases.loading")));

        player.stop();

        if (!player.checkConnect(event))
            player.connect(event.getMember().getVoiceState().getChannel());

        player.getAudioPlayerManager().loadItem(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.play(track);
                SafeMessage.editMessage(infoMessage, success(event.translate("phrases.loaded"), event.translate("command.texttospeech")));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
            }

            @Override
            public void noMatches() {
                SafeMessage.editMessage(infoMessage, error(event.translate("phrases.error"), event.translate("phrases.error.unknown")));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                SafeMessage.editMessage(infoMessage, error(event.translate("phrases.error"), event.translate("phrases.error.unknown")));
            }
        });

        return null;
    }

    private OptionalInt getNumberFromString(String string) {
        try {
            return OptionalInt.of(Integer.parseInt(string));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    private String buildUrl(String text, Language language, int speed) throws UnsupportedEncodingException {
        return String.format("https://api.voicerss.org/?key=%s&hl=%s&src=%s&f=48khz_16bit_stereo&c=mp3&r=%s",
                GroovyBot.getInstance().getConfig().getJSONObject("voice_rss").getString("api_key"), language.code, URLEncoder.encode(text, "UTF-8"), speed);
    }

    private enum Language {
        CA_ES("ca-es"),
        ZH_CN("zh-cn"),
        ZH_HK("zh-hk"),
        ZH_TW("zh-tw"),
        DA_DK("da-dk"),
        NL_NL("nl-nl"),
        EN_AU("en-au"),
        EN_CA("en-ca"),
        EN_GB("en-gb"),
        EN_IN("en-in"),
        EN_US("en-us"),
        FI_FI("fi-fi"),
        FR_CA("fr-ca"),
        FR_FR("fr-fr"),
        DE_DE("de-de"),
        IT_IT("it-it"),
        JA_JP("ja-jp"),
        KO_KR("ko-kr"),
        NB_NO("nb-no"),
        PL_PL("pl-pl"),
        PT_BR("pt-br"),
        PT_PT("pt-pt"),
        RU_RU("ru-ru"),
        ES_MX("es-mx"),
        ES_ES("es-es"),
        SV_SE("sv-se"),
        UNKNOWN("");

        private final String code;

        Language(String code) {
            this.code = code;
        }

        private static Language fromCode(String code) {
            for (Language lang : Language.values()) {
                if (lang.code.equals(code)) {
                    return lang;
                }
            }

            return Language.UNKNOWN;
        }
    }
}
