package com.fr.swift.quartz.service;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.ConfigInputUtil;
import com.fr.swift.groovy.GroovyFactory;
import com.fr.swift.groovy.schedule.ScheduleGroovyJobWrapper;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.quartz.config.ScheduleTaskType;
import com.fr.swift.quartz.entity.TaskDefine;
import com.fr.swift.quartz.execute.ScheduleJob;
import groovy.lang.GroovyObject;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/9/3
 */
@SwiftBean(name = "scheduleTaskService")
public class ScheduleTaskServiceImpl extends AbstractLifeCycle implements ScheduleTaskService {

    private final static String GROOVY_PATH = "/groovy";

    private final static String SCHEDULE_PATH = "/schedule";

    private final static String CUSTOM_PATH = "/custom";

    private QuartzJobService quartzJobService;

    public ScheduleTaskServiceImpl() {
        this.quartzJobService = new QuartzJobServiceImpl();
    }

    @Override
    public void startup() throws LifeCycleException {
        super.startup();
        quartzJobService.startup();
        try {
            initGroovyScheduleJob();
        } catch (Exception e) {
            throw new LifeCycleException(e);
        }
    }

    @Override
    public void shutdown() throws LifeCycleException {
        super.shutdown();
        quartzJobService.shutdown();
    }

    @Override
    public Runnable getGroovyExecuteJob(String fileName) throws Exception {
        File groovyPath = getPathFile(GROOVY_PATH);
        File file = new File(groovyPath.getAbsolutePath() + CUSTOM_PATH + "/" + fileName);
        if (!file.exists()) {
            SwiftLoggers.getLogger().warn("{} not exist!", file.getAbsolutePath());
            return null;
        }
        GroovyObject groovyObject = loadGroovyObject(file);
        return () -> groovyObject.invokeMethod("execute", null);
    }

    @Override
    public void initGroovyScheduleJob() throws Exception {
        File groovyPath = getPathFile(GROOVY_PATH);
        File[] files = new File(groovyPath.getAbsolutePath() + SCHEDULE_PATH).listFiles();
        for (File scheduleJobFile : files) {
            TaskDefine taskDefine = parseTaskDefine(scheduleJobFile);
            addOrUpdateJob(taskDefine);
        }
    }

    @Override
    public void addOrUpdateGroovyJobs(String... fileNames) throws Exception {
        File groovyPath = getPathFile(GROOVY_PATH);
        for (String fileName : fileNames) {
            File file = new File(groovyPath.getAbsolutePath() + SCHEDULE_PATH + "/" + fileName);
            if (!file.exists()) {
                SwiftLoggers.getLogger().warn("{} not exist!", file.getAbsolutePath());
                continue;
            }
            TaskDefine taskDefine = parseTaskDefine(file);
            addOrUpdateJob(taskDefine);
        }
    }

    @Override
    public void deleteGroovyJobs(String... fileNames) throws Exception {
        File groovyPath = getPathFile(GROOVY_PATH);
        for (String fileName : fileNames) {
            File file = new File(groovyPath.getAbsolutePath() + SCHEDULE_PATH + "/" + fileName);
            if (!file.exists()) {
                SwiftLoggers.getLogger().warn("{} not exist!", file.getAbsolutePath());
                continue;
            }
            TaskDefine taskDefine = parseTaskDefine(file);
            deleteJob(taskDefine.getJobKey());
        }
    }

    private void addOrUpdateJob(TaskDefine define) throws SchedulerException {
        ensureStarted();
        if (define != null) {
            quartzJobService.scheduleJob(define);
        }
    }

    private boolean deleteJob(JobKey jobKey) throws Exception {
        ensureStarted();
        return quartzJobService.deleteJob(jobKey);
    }

    /**
     * get exist path file, include out and inner groovy directory
     *
     * @param dirName
     * @return
     */
    private File getPathFile(String dirName) {
        File outFile = new File(new File("").getAbsolutePath() + dirName);
        if (outFile.exists()) {
            return outFile;
        } else {
            File inFile = new File(ConfigInputUtil.class.getResource("/").getPath() + dirName);
            return inFile;
        }
    }

    private TaskDefine parseTaskDefine(File scheduleJobFile) throws ResourceException, ScriptException, InstantiationException, IllegalAccessException, IOException {
        GroovyObject groovyObject = loadGroovyObject(scheduleJobFile);
        ScheduleJob job = new ScheduleGroovyJobWrapper(groovyObject);
        if (job.getExecutorType().equals(ScheduleTaskType.ALL) ||
                SwiftProperty.get().getMachineId().equals(job.getExecutorType().getMachineId())) {
            JobKey jobKey = JobKey.jobKey((String) groovyObject.invokeMethod("jobKey", null));
            Map<?, ?> jobDataMap = Collections.singletonMap("groovyObject", groovyObject);
            TaskDefine task = TaskDefine.builder()
                    .jobKey(jobKey)
                    .cronExpression(job.getCronExpression())
                    .jobClass(job.getClass())
                    .build();
            task.setJobDataMap(jobDataMap);
            return task;
        }
        return null;
    }

    private GroovyObject loadGroovyObject(File scheduleJobFile) throws IOException, ScriptException, InstantiationException, ResourceException, IllegalAccessException {
        SwiftLoggers.getLogger().info("Init schedule file {}", scheduleJobFile.getAbsolutePath());
        GroovyObject groovyObject = GroovyFactory.get().parseGroovyObject(scheduleJobFile);
        return groovyObject;
    }
}
