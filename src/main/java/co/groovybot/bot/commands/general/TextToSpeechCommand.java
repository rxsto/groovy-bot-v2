package co.groovybot.bot.commands.general;

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
import net.dv8tion.jda.core.utils.Helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
@Log4j2
public class TextToSpeechCommand extends SemiInChannelCommand {

    public TextToSpeechCommand() {
        super(new String[]{"tts"}, CommandCategory.GENERAL, Permissions.tierOne(), "Converts text into spoken voice.", "[speed] [language] <text>");
    }

    @Override
    protected Result executeCommand(String[] args, CommandEvent event, MusicPlayer player) {
        if (args.length == 0) {
            return sendHelp();
        }

        Message infoMessage = SafeMessage.sendMessageBlocking(event.getChannel(), info(event.translate("command.tts.loading.title"), event.translate("command.tts.loading.description")));

        int speed = 0;
        String speedString = args[0];
        try {
            speed = Integer.parseInt(speedString);
            args[0] = "";
            if (speed < -10 || speed > 10) {
                SafeMessage.editMessage(infoMessage, error(event.translate("command.tts.invalidnumber.title"), event.translate("command.tts.invalidnumber.description")));
                return null;
            }
        } catch (NumberFormatException ignored) {
        }


        Language language = Language.EN_US;
        if (args.length > 1) {
            language = Language.fromCode(args[1]);
            if (language != Language.UNKNOWN) {
                args[1] = "";
            }
        }

        String text = String.join(" ", args).replace(",", "");

        String url;
        try {
            url = buildUrl(text, language, speed);
        } catch (UnsupportedEncodingException e) {
            log.error(e);
            SafeMessage.editMessage(infoMessage, error(event.translate("phrases.error.unknown.title"), event.translate("phrases.error.unknown.description")));
            return null;
        }

        player.stop();
        player.getAudioPlayerManager().loadItem(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.play(track);
                SafeMessage.editMessage(infoMessage, success(event.translate("command.tts.loaded.title"), event.translate("command.tts.loaded.title")));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
            }

            @Override
            public void noMatches() {
                SafeMessage.editMessage(infoMessage, error(event.translate("phrases.error.unknown.title"), event.translate("phrases.error.unknown.description")));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                SafeMessage.editMessage(infoMessage, error(event.translate("phrases.error.unknown.title"), event.translate("phrases.error.unknown.description")));
            }
        });

        return null;
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
        UNKNOWN("en-us");

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
