//package com.train.batch.config;
//
//import com.train.batch.job.QuartzDemo;
//import org.quartz.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class QuartzConfig {
//
//    @Bean
//    public JobDetail jobDetail() {
//         return JobBuilder.newJob(QuartzDemo.class)
//                 .withIdentity("QuartzDemo", "test-quartz")
//                 .storeDurably()
//                 .build();
//    }
//
//    @Bean
//    public Trigger trigger() {
//         return TriggerBuilder.newTrigger()
//                 .forJob(jobDetail())
//                 .withIdentity("trigger", "trigger")
//                 .startNow()
//                 .withSchedule(CronScheduleBuilder.cronSchedule("*/2 * * * * ?"))
//                 .build();
//    }
//}
