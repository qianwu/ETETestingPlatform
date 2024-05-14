package org.charlotte.e2ecore.job;

import org.charlotte.e2ecore.service.ChainDataService;
import org.charlotte.e2ecore.service.LockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ：charlotte
 * @date ：Created in 13/1/22 3:33 PM
 * @description ：
 */
@Slf4j
@Service
public class ChainDataJob {

    private static final String OVERTIME_CALLBACK_LOCK = "OVERTIME_CALLBACK_LOCK";


    @Resource
    private LockService lockService;

    @Resource
    private ChainDataService chainDataService;

    @Scheduled(cron = "${task.cron.overtimeChainDataCallbackJob}")
    public void overtimeChainDataCallbackJob() {
        //lock
        String lockToken = lockService.acquire(OVERTIME_CALLBACK_LOCK);
        if (StringUtils.isEmpty(lockToken)) {
            log.info("Fail to acquire OVERTIME_CALLBACK_LOCK!");
            return;
        }
        log.info("acquire OVERTIME_CALLBACK_LOCK! token={}", lockToken);

        try {
            chainDataService.doScheduleOvertimeChainDataCallback();
        } catch (Exception e) {
            log.error("overtimeChainDataCallbackJob fail", e);
        } finally {
            //unlock
            lockService.release(OVERTIME_CALLBACK_LOCK, lockToken);
            log.info("release OVERTIME_CALLBACK_LOCK!");
        }


    }


}
