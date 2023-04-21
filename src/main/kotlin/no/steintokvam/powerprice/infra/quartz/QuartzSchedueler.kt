package no.steintokvam.powerprice.infra.quartz

import no.steintokvam.powerprice.infra.quartz.jobs.GetPricesJob
import org.quartz.*
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class QuartzSchedueler {

    private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)

    fun schedueleJobs() {
        LOGGER.info("Scheduling jobs.")
        val scheduelerFactory: SchedulerFactory = StdSchedulerFactory()
        val schedueler: Scheduler = scheduelerFactory.scheduler
        schedueler.start()

        scheduleJob(createPriceJobTrigger(), createPriceJobDetail(), schedueler)
    }

    private fun scheduleJob(trigger: Trigger, jobDetail: JobDetail, schedueler: Scheduler) {
        schedueler.scheduleJob(jobDetail, trigger)
        LOGGER.info("Job scheduled for " + trigger.nextFireTime.toString())
    }

    private fun createPriceJobTrigger(): Trigger {
        return newTrigger()
            .withIdentity("getPriceTrigger", "getPriceTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 15 14 ? * * *"))//At 14:15:00pm every day
            .build()
    }

    private fun createPriceJobDetail(): JobDetail {
        return JobBuilder.newJob(GetPricesJob::class.java).withIdentity("createPriceJob", "createPriceGroup").build()
    }
}