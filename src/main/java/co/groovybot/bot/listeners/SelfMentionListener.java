package co.groovybot.bot.listeners;

import co.groovybot.bot.GroovyBot;
import co.groovybot.bot.core.entity.EntityProvider;
import co.groovybot.bot.util.Colors;
import co.groovybot.bot.util.EmbedUtil;
import co.groovybot.bot.util.SafeMessage;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class SelfMentionListener {

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onMention(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals(event.getGuild().getSelfMember().getAsMention())) {
            String prefix = EntityProvider.getGuild(event.getGuild().getIdLong()) == null ? EntityProvider.getGuild(event.getGuild().getIdLong()).getPrefix() : GroovyBot.getInstance().getConfig().getJSONObject("settings").getString("prefix");
            SafeMessage.sendMessage(event.getChannel(), EmbedUtil.noTitle(String.format("<a:hey:526016403694813195> **Hey!** My **prefix** is **`%s`**, you'll get **my commands** with **`%shelp`**", prefix, prefix)));
        }
    }
}
