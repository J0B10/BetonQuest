package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.modules.schedule.ScheduleID;

import java.util.List;

/**
 * Schedules define a list of events that will be run at specific times.
 *
 * <p>
 * While this Interface provides basic information about a schedule like the list of events that will be run or the
 * time at which they will be run next, the actual scheduling logic is defined by the {@link Scheduler}.
 * </p>
 * <p>
 * Different types of schedules may use different data types to handle time.
 * For example realtime schedules use {@link java.time.Instant} while ingame schedules might use an Integer
 * representing the amount of ticks since the start of the server.
 * </p>
 */
public interface Schedule {
    /**
     * Get the Identifier of this schedule.
     *
     * @return the id
     */
    ScheduleID getId();

    /**
     * Get a list of events that will be run by this schedule.
     *
     * @return unmodifiable list of events
     */
    List<EventID> getEvents();

    /**
     * Get how the scheduler should behave if an execution of the schedule was missed
     * (e.g. due to a shutdown of the server).
     *
     * @return the catchup strategy, {@link CatchupStrategy#NONE} by default
     */
    CatchupStrategy getCatchup();
}
