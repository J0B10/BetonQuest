package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class BreedObjective extends CountingObjective implements Listener {

    private final EntityType type;

    public BreedObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "animals_to_breed");
        type = instruction.getEntity();
        targetAmount = instruction.getPositive();
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onBreeding(final EntityBreedEvent event) {
        if (event.getEntityType() == type && event.getBreeder() instanceof Player) {
            final String playerID = PlayerConverter.getID((Player) event.getBreeder());
            if (containsPlayer(playerID) && checkConditions(playerID)) {
                getCountingData(playerID).progress();
                completeIfDoneOrNotify(playerID);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

}
