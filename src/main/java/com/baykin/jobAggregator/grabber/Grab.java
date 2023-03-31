package com.baykin.jobAggregator.grabber;

import com.baykin.jobAggregator.dao.Store;
import com.baykin.jobAggregator.utils.Parse;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}
