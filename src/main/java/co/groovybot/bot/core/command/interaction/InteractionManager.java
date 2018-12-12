package co.groovybot.bot.core.command.interaction;

import lombok.Getter;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class InteractionManager {

    @Getter
    private final Map<Long, InteractableMessage> interactionStorage;

    public InteractionManager() {
        this.interactionStorage = new HashMap<>();
    }

    protected void register(InteractableMessage message) {
        interactionStorage.put(message.getIdentifier(), message);
    }

    public void unregister(InteractableMessage message) {
        interactionStorage.remove(message.getIdentifier());
    }

    protected void update(InteractableMessage message) {
        interactionStorage.replace(message.getIdentifier(), message);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onReaction(GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;
        if (!isInteractable(event.getMessageIdLong())) return;
        InteractableMessage interactableMessage = interactionStorage.get(event.getMessageIdLong());
        event.getReaction().removeReaction(event.getUser()).queue();
        if (!checkAuthor(interactableMessage, event.getUser())) return;
        interactableMessage.handleReaction(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onMessage(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        final long authorId = event.getAuthor().getIdLong();
        if (!isInteractable(authorId)) return;
        InteractableMessage interactableMessage = interactionStorage.get(authorId);
        event.getMessage().delete().queue();
        if (!checkAuthor(interactableMessage, event.getAuthor())) return;
        interactableMessage.handleMessage(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onMessageDeletion(GuildMessageDeleteEvent event) {
        interactionStorage.remove(event.getMessageIdLong());
    }

    private boolean isInteractable(long message) {
        return interactionStorage.containsKey(message);
    }

    private boolean checkAuthor(InteractableMessage message, User author) {
        return message.getAuthor().getUser().equals(author);
    }
}
