package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// 配置 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {

    // FactoryBean可简化Bean的实例化过程:
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.该Bean得到的是FactoryBean所管理的对象实例.

    // 配置JobDetail
    // @Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob"); // 任务名称
        factoryBean.setGroup("alphaJobGroup"); // 任务组
        factoryBean.setDurability(true); // 持久保存
        factoryBean.setRequestsRecovery(true); // 可恢复
        return factoryBean;
    }

    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
    // @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail); // 设置任务详情
        factoryBean.setName("alphaTrigger"); // 触发器名称
        factoryBean.setGroup("alphaTriggerGroup"); // 触发器组
        factoryBean.setRepeatInterval(3000); // 时间间隔
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    // 刷新帖子分数的任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob"); // 任务名称
        factoryBean.setGroup("communityJobGroup"); // 任务组
        factoryBean.setDurability(true); // 持久保存
        factoryBean.setRequestsRecovery(true); // 可恢复
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshJobTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail); // 设置任务详情
        factoryBean.setName("postScoreRefreshTrigger"); // 触发器名称
        factoryBean.setGroup("communityTriggerGroup"); // 触发器组
        factoryBean.setRepeatInterval(1000 * 60 * 5); // 时间间隔
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
