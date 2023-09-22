package org.betonquest.betonquest.api.schedule;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.modules.schedule.ScheduleID;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all schedulers.
 *
 * <p>
 * Maintains a map of all schedules that belong to this scheduler, provides a basic implementation
 * to execute all events of a schedule and provides a flag that indicates if the scheduler is currently running.
 * </p>
 *
 * @param <S> Type of Schedule that this scheduler manages
 */
public class BaseScheduler<S extends Schedule> implements Scheduler<S> {
    /**
     * Map containing all schedules that belong to this scheduler.
     */
    protected final Map<ScheduleID, S> schedules;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Flag stating if this scheduler is currently running.
     */
    private boolean running;

    /**
     * Default constructor.
     *
     * @param log the logger that will be used for logging
     */
    public BaseScheduler(final BetonQuestLogger log) {
        this.log = log;
        schedules = new HashMap<>();
        running = false;
    }

    @Override
    public void addSchedule(final S schedule) {
        schedules.put(schedule.getId(), schedule);
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
        schedules.clear();
    }

    @Override
    public void executeEvents(final S schedule) {
        log.debug(schedule.getId().getPackage(), "Schedule '" + schedule.getId() + "' runs its events...");
        for (final EventID eventID : schedule.getEvents()) {
            BetonQuest.event(null, eventID);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
