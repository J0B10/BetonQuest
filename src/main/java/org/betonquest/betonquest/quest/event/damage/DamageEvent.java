package org.betonquest.betonquest.quest.event.damage;

import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

/**
 * The damage event. It damages the player.
 */
public class DamageEvent implements Event {
    /**
     * Amount of damage to inflict.
     */
    private final VariableNumber damage;

    /**
     * Create a damage event that inflicts the given amount of damage to the player.
     *
     * @param damage damage to inflict
     */
    public DamageEvent(final VariableNumber damage) {
        this.damage = damage;
    }

    @Override
    public void execute(final String playerId) throws QuestRuntimeException {
        final double calculatedDamage = Math.abs(damage.getDouble(playerId));
        final Player player = PlayerConverter.getPlayer(playerId);
        player.damage(calculatedDamage);
    }
}
