package com.github.howaric.docker_rapido.utils;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class PollExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PollExecutor.class);

    /**
     * @param interval interval time between each polling
     * @param duration total polling time
     * @param unit     time unit of interval and duration
     * @param pollJob  return null to continue poll, return not null for stopping
     *                 poll job
     * @return
     */
    public static <T> T poll(Integer interval, Integer duration, TimeUnit unit, Callable<T> pollJob) {
        Stopwatch timer = Stopwatch.createStarted();
        long durationMillis = unit.toMillis(duration);
        long intervalMillis = unit.toMillis(interval);
        int index = 0;
        try {
            while (true) {
                index++;
                T result = pollJob.call();
                if (result != null) {
                    return result;
                }
                if (timer.elapsed(TimeUnit.MILLISECONDS) >= durationMillis) {
                    LOGGER.debug("Poll job timeout");
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(intervalMillis);
            }
        } catch (Exception e) {
            LOGGER.error("Poll job in PollExecutor failed", e);
        } finally {
            LOGGER.debug("Poll job stopped, total poll times:{}", index);
            timer.stop();
        }
        return null;
    }

    /**
     * @param interval interval time between each polling, seconds as time unit
     * @param duration total polling time, seconds as time unit
     * @param pollJob  return null to continue poll, return not null for stopping
     *                 poll job
     * @return
     */
    public static <T> T poll(Integer interval, Integer duration, Callable<T> pollJob) {
        return poll(interval, duration, TimeUnit.SECONDS, pollJob);
    }
}
