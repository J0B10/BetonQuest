package org.betonquest.betonquest.compatibility.denizen;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.scripts.containers.core.TaskScriptContainer;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

/**
 * Runs specified Denizen task script.
 */
@SuppressWarnings("PMD.CommentRequired")
public class DenizenTaskScriptEvent extends QuestEvent {

    private final String name;

    public DenizenTaskScriptEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        name = instruction.next();
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final TaskScriptContainer script = ScriptRegistry.getScriptContainerAs(name, TaskScriptContainer.class);
        if (script == null) {
            throw new QuestRuntimeException("Could not find '" + name + "' Denizen script");
        }
        if (profile.getPlayer().isEmpty()) {
            throw new QuestRuntimeException("Player is offline");
        }
        final Player player = profile.getPlayer().get();
        final BukkitScriptEntryData data = new BukkitScriptEntryData(PlayerTag.mirrorBukkitPlayer(player), null);
        script.run(data, null);
        return null;
    }

}
