package org.betonquest.betonquest.api.schedule;

/**
 * A scheduler manages all schedules of a specific type and is responsible for executing them at the correct time.
 *
 * <p>
 * When loading the configs,
 * new schedules are parsed and registered in the matching Scheduler by calling {@link #addSchedule(Schedule)}.
 * After everything is loaded {@link #start()} is called. It should start the scheduler.
 * Once a time defined in the schedule is met,
 * the referenced events shall be executed using {@link #executeEvents(Schedule)}.
 * On shutdown or before reloading all data, {@link #stop()} is called to stop all schedules.
 * Also, this class should implement the {@link CatchupStrategy} required by the schedule.
 * </p>
 *
 * @param <S> Type of Schedule that this scheduler manages
 */
public interface Scheduler<S extends Schedule> {

    /**
     * Register a new schedule to the list of schedules managed by this scheduler.
     * The schedule shall remain inactive till method {@link #start()} is called to activate all schedules.
     *
     * @param schedule schedule object to register
     */
    void addSchedule(S schedule);

    /**
     * <p>
     * Start all schedules that have been added to this scheduler.
     * This method is called on startup and reload of BetonQuest to activate/resume all schedules.
     * </p>
     * <p>
     * As well as handling the actual scheduling logic this method shall also handle catching up schedules that
     * were missed during reloading or shutdown of the server, based on their {@link CatchupStrategy}.
     * </p>
     * <p><b>
     * When overriding this method, make sure to call {@code super.start()} at some point to update the running flag.
     * </b></p>
     */
    void start();

    /**
     * <p>
     * Stop the scheduler and unregister all schedules that belong to this scheduler.
     * Typically this method is called on reload and server shutdown.
     * </p>
     * <p><b>
     * When overriding this method, make sure to call {@code super.stop()} at some point to clear the map of schedules.
     * </b></p>
     */
    void stop();

    /**
     * This method shall be called whenever the execution time of a schedule is reached.
     * It executes all events that should be run by the schedule.
     *
     * @param schedule a schedule that reached execution time, providing a list of events to run
     */
    void executeEvents(S schedule);

    /**
     * Check if this scheduler is currently running.
     *
     * @return true if currently running, false if not (e.g. during startup or reloading)
     */
    boolean isRunning();
}
