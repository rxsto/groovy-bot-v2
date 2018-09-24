package io.groovybot.bot.core.command.interaction;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class InteractionManager {

    private final Map<Long, InteractableMessage> interactionStorage;

    public InteractionManager() {
        this.interactionStorage = new HashMap<>();
    }

    protected void register(InteractableMessage message) {
        interactionStorage.put(message.getInfoMessage().getIdLong(), message);
    }

    public void unregister(InteractableMessage message) {
        interactionStorage.remove(message.getInfoMessage().getIdLong());
    }

    protected void update(InteractableMessage message) {
        interactionStorage.replace(message.getInfoMessage().getIdLong(), message);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onReaction(GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;
        if (!isInteractable(event)) return;
        InteractableMessage interactableMessage = interactionStorage.get(event.getMessageIdLong());
        event.getReaction().removeReaction(event.getUser()).queue();
        if (!checkAuthor(interactableMessage, event.getUser())) return;
        interactableMessage.handleReaction(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onMessageDeletion(GuildMessageDeleteEvent event) {
        interactionStorage.remove(event.getMessageIdLong());
    }

    private boolean isInteractable(GenericGuildMessageEvent event) {
        return interactionStorage.containsKey(event.getMessageIdLong());
    }

    private boolean checkAuthor(InteractableMessage message, User author) {
        return message.getAuthor().getUser().equals(author);
    }
}
