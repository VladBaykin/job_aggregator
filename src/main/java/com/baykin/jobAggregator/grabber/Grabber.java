package com.baykin.jobAggregator.grabber;

import com.baykin.jobAggregator.grabber.Grab;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import com.baykin.jobAggregator.dao.*;
import com.baykin.jobAggregator.entity.Post;
import com.baykin.jobAggregator.utils.*;
import com.baykin.jobAggregator.html.HabrCareerParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {

    private static final Logger LOG = LoggerFactory.getLogger(Grabber.class.getName());
    private PropertiesUtil config;
    private Connection cn;

    public Store store() {
        return new PsqlStore(config);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg(String path) {
        config = new PropertiesUtil(path);
        cn = new ConnectionManager(config).open();
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        data.put("parseUrl", config.get("parseUrl"));
        data.put("amountPage", config.get("amountPage"));
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(config.get("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server =
                         new ServerSocket(Integer.parseInt(config.get("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream()) {
                        out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                        for (Post post : store.getAll()) {
                            out.write(
                                    post.toString().getBytes(Charset.forName("Windows-1251"))
                            );
                            out.write((System.lineSeparator().getBytes()));
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("Error server", e);
            }
        }).start();
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            String parseUrl = (String) map.get("parseUrl");
            String amountPage = (String) map.get("amountPage");
            List<Post> postList = parse.list(parseUrl, Integer.parseInt(amountPage));
            for (Post post : postList) {
                store.save(post);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg("application.properties");
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new HabrCareerParse(new HabrCareerDateTimeParser()), store, scheduler);
        grab.web(store);
    }
}