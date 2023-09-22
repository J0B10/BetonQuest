package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.modules.schedule.ScheduleID;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Base class for all schedules.
 *
 * <p>
 * Stores and provides access to the schedule's id, time, events and catchup strategy.
 * </p>
 */
@SuppressWarnings({"PMD.AbstractClassWithoutAbstractMethod", "PMD.DataClass"})
public abstract class BaseSchedule implements Schedule {

    /**
     * Identifier of this schedule.
     */
    protected final ScheduleID scheduleID;

    /**
     * Instruction string defining at which time the events should be scheduled to run.
     */
    protected final String time;

    /**
     * A list of events that will be run by this schedule.
     */
    protected final List<EventID> events;

    /**
     * Defines how the scheduler should behave if an execution of the schedule was missed
     * (e.g. due to a shutdown of the server).
     * Should be None by default.
     */
    protected final CatchupStrategy catchup;

    /**
     * Creates new instance of the schedule.
     * It should parse all options from the configuration section.
     * If anything goes wrong, throw {@link InstructionParseException} with an error message describing the problem.
     *
     * @param scheduleID  id of the new schedule
     * @param instruction config defining the schedule
     * @throws InstructionParseException if parsing the config failed
     */
    public BaseSchedule(final ScheduleID scheduleID, final ConfigurationSection instruction) throws InstructionParseException {
        this.scheduleID = scheduleID;

        this.time = Optional.ofNullable(instruction.getString("time"))
                .orElseThrow(() -> new InstructionParseException("Missing time instruction"));

        final String eventsString = Optional.ofNullable(instruction.getString("events"))
                .orElseThrow(() -> new InstructionParseException("Missing events"));
        final List<EventID> events = new ArrayList<>();
        for (final String eventId : eventsString.split(",")) {
            try {
                events.add(new EventID(scheduleID.getPackage(), eventId));
            } catch (final ObjectNotFoundException e) {
                throw new InstructionParseException("Error while loading events: " + e.getMessage(), e);
            }
        }
        this.events = Collections.unmodifiableList(events);

        final String catchupString = instruction.getString("catchup");
        try {
            this.catchup = Optional.ofNullable(catchupString).map(String::toUpperCase).map(CatchupStrategy::valueOf).orElse(CatchupStrategy.NONE);
        } catch (final IllegalArgumentException e) {
            throw new InstructionParseException("There is no such catchup strategy: " + catchupString, e);
        }
    }

    /**
     * Get the Instruction string defining at which time the events should be scheduled to run.
     *
     * @return string defined with key {@code time } in the config section of the schedule
     */
    public String getTime() {
        return time;
    }

    @Override
    public ScheduleID getId() {
        return scheduleID;
    }

    @Override
    public List<EventID> getEvents() {
        return events;
    }

    @Override
    public CatchupStrategy getCatchup() {
        return catchup;
    }
}
