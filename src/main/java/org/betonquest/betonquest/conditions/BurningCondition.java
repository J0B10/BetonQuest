package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Requires the player to burn
 */
public class BurningCondition extends Condition {

    /**
     * Constructor of the BurningCondition
     *
     * @param instruction the instruction
     */
    public BurningCondition(final Instruction instruction) {
        super(instruction, true);
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        if (profile.getPlayer().isEmpty()) {
            throw new QuestRuntimeException("Player is offline");
        }
        return profile.getPlayer().get().getFireTicks() > 0;
    }
}
