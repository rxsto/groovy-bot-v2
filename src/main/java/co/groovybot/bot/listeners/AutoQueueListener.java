package co.groovybot.bot.listeners;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.audio.MusicPlayer;
import co.groovybot.bot.core.command.CommandEvent;
import co.groovybot.bot.core.command.interaction.InteractableMessage;
import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.SafeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

@RequiredArgsConstructor
@Log4j2
@SuppressWarnings("unused")
public class AutoQueueListener {

    private final GroovyBot bot;

    @SubscribeEvent
    private void handleURLMessage(GuildMessageReceivedEvent event) {
        if (!event.getMessage().getContentDisplay().matches("([a-zA-Z\\d]+:\\/\\/)?((\\w+:\\w+@)?([a-zA-Z\\d-]*\\.)?([a-zA-Z\\d.-]+\\.[A-Za-z]{2,13})(:\\d+)?(\\/\\S*)?)"))
            return;
        MusicPlayer player = bot.getMusicPlayerManager().getPlayer(event.getGuild(), event.getChannel());
        if (player == null)
            return;

        String searchItem;
        if (event.getMessage().getContentDisplay().matches("(https?://)?(.*)?spotify\\.com.*"))
            searchItem = player.removeQueryFromUrl(event.getMessage().getContentDisplay());
        else
            searchItem = event.getMessage().getContentDisplay();
        event.getMessage().addReaction("⏯").complete();
        new ReactionMenu(event.getMessage(), event.getChannel(), event.getMember(), event.getAuthor().getIdLong(), searchItem, event, player);

    }

    private class ReactionMenu extends InteractableMessage {

        private final String searchItem;
        private final GuildMessageReceivedEvent e;
        private final MusicPlayer player;

        ReactionMenu(Message infoMessage, TextChannel channel, Member author, long identifier, String item, GuildMessageReceivedEvent e, MusicPlayer player) {
            super(infoMessage, channel, author, identifier);
            this.searchItem = item;
            this.e = e;
            this.player = player;
        }

        @Override
        protected void handleReaction(GuildMessageReactionAddEvent event) {
            final String reactionRaw = event.getReactionEmote().getName();
            if (reactionRaw.equals("⏯")) {
                CommandEvent eve = new CommandEvent(this.e, bot, new String[]{searchItem}, "listenerFillingSystem");
                if(player.checkConnect(eve)) {
                    player.connect(eve.getMember().getVoiceState().getChannel());
                    player.queueSongs(eve);
                }else {
                    SafeMessage.sendMessage(event.getChannel(), EmbedUtil.error(translate(event.getUser(),"phrases.notconnected.title"), translate(event.getUser(), "phrases.notconnected.description")), 10);
                }

            }
        }
    }

}
