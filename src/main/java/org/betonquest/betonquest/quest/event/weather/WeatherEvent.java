package org.betonquest.betonquest.quest.event.weather;

import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.World;

/**
 * The weather event. It sets the weather to a defined state.
 */
public class WeatherEvent implements Event {
    /**
     * Whether it should rain.
     */
    private final boolean storm;
    /**
     * Whether it should thunder.
     */
    private final boolean thunder;

    /**
     * Create the weather event to set the given state.
     *
     * @param storm the storm (rain) state to set
     * @param thunder the thunder state to set
     */
    public WeatherEvent(final boolean storm, final boolean thunder) {
        this.storm = storm;
        this.thunder = thunder;
    }

    @Override
    public void execute(final String playerId) {
        final World world = PlayerConverter.getPlayer(playerId).getWorld();
        world.setStorm(storm);
        world.setThundering(thunder);
    }
}
