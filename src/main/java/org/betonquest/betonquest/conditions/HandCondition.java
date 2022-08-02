package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Holding item in hand condition
 */
@SuppressWarnings("PMD.CommentRequired")
public class HandCondition extends Condition {

    private final QuestItem questItem;
    private final boolean offhand;

    public HandCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        questItem = new QuestItem(instruction.getItem());
        offhand = instruction.hasArgument("offhand");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        if (profile.getPlayer().isEmpty()) {
            throw new QuestRuntimeException("Player is offline");
        }
        final PlayerInventory inv = profile.getPlayer().get().getInventory();
        final ItemStack item = offhand ? inv.getItemInOffHand() : inv.getItemInMainHand();

        return questItem.compare(item);
    }

}
