package org.betonquest.betonquest.quest.event.weather;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.OnlinePlayerRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Factory to create weather events from {@link Instruction}s.
 */
public class WeatherEventFactory implements EventFactory {
    /**
     * Server to use for syncing to the primary server thread.
     */
    private final Server server;
    /**
     * Scheduler to use for syncing to the primary server thread.
     */
    private final BukkitScheduler scheduler;
    /**
     * Plugin to use for syncing to the primary server thread.
     */
    private final Plugin plugin;

    /**
     * Create the weather event factory.
     *
     * @param server server to use
     * @param scheduler scheduler to use
     * @param plugin plugin to use
     */
    public WeatherEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadEvent(
                new OnlinePlayerRequiredEvent(
                        createWeatherEvent(instruction), instruction.getPackage()),
                server, scheduler, plugin);
    }

    @NotNull
    private Event createWeatherEvent(final Instruction instruction) throws InstructionParseException {
        final String weather = instruction.next();
        return switch (weather.toLowerCase(Locale.ROOT)) {
            case "sun" -> createSunEvent();
            case "rain" -> createRainEvent();
            case "storm" -> createStormEvent();
            case "thunder" -> createThunderEvent();
            default ->
                    throw new InstructionParseException("Unknown weather state (valid options are: sun, clear, rain, storm, thunder):" + weather);
        };
    }

    private Event createSunEvent() {
        return new WeatherEvent(false, false);
    }

    private Event createRainEvent() {
        return new WeatherEvent(true, false);
    }

    private Event createStormEvent() {
        return new WeatherEvent(true, true);
    }

    private Event createThunderEvent() {
        return new WeatherEvent(false, true);
    }
}
