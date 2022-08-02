package org.betonquest.betonquest.conversation;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

@SuppressWarnings("PMD.CommentRequired")
public class NonInterceptingInterceptor implements Interceptor {

    protected final Conversation conv;
    protected final Player player;

    public NonInterceptingInterceptor(final Conversation conv, final Profile profile) throws QuestRuntimeException {
        if (profile.getPlayer().isEmpty()) {
            throw new QuestRuntimeException("Player is offline");
        }
        this.conv = conv;
        this.player = profile.getPlayer().get();
    }

    @Override
    public void sendMessage(final String message) {
        player.spigot().sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessage(final BaseComponent... message) {
        player.spigot().sendMessage(message);
    }

    @Override
    public void end() {
        // Empty
    }
}
