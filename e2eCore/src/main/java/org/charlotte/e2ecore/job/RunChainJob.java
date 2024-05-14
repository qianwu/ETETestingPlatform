package org.charlotte.e2ecore.job;

import org.charlotte.e2ecore.service.ChainDataService;
import org.charlotte.e2ecore.service.ChainService;
import org.charlotte.e2ecore.service.ExecuteService;
import org.charlotte.e2ecore.service.LockService;
import org.charlotte.e2edomain.Chain;
import org.charlotte.e2edomain.ChainData;
import org.charlotte.e2edomain.dto.ChainBatchQueryDTO;
import org.charlotte.e2edomain.dto.ChainExecDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author ：charlotte
 * @date ：Created in 7/6/22 2:00 PM
 * @description ： All chains are executed automatically at a specified time of day
 */
@Slf4j
@Service
public class RunChainJob {

    private static final String AUTO_START_CHAIN_LOCK = "AUTO_START_CHAIN_LOCK";

    @Resource
    private ExecuteService executeService;

    @Resource
    private ChainDataService chainDataService;

    @Resource
    private ChainService chainService;

    @Resource
    private LockService lockService;

    @Scheduled(cron = "${task.cron.autoExecuteAllChainJob}")
    public void runAllChainAtSpecifiedTimeJob() {
        log.info("start to run all chain ...");
        //lock
        String lockToken = lockService.acquire(AUTO_START_CHAIN_LOCK);
        if (StringUtils.isEmpty(lockToken)) {
            log.info("Fail to acquire AUTO_START_CHAIN_LOCK!");
            return;
        }

        log.info("acquire AUTO_START_CHAIN_LOCK! token={}", lockToken);

        try {
            List<Chain> chainList = chainService.queryChainByConditions(new ChainBatchQueryDTO());

            for (int i = 0; i < chainList.size(); i++) {
                if ("test-chain".equals(chainList.get(i).getName())) {
                    continue;
                }

                ChainExecDTO chainExecDTO = new ChainExecDTO();
                chainExecDTO.setChainId(String.valueOf(chainList.get(i).getId()));
                chainExecDTO.setEnv("uat"); // todo:1. add env configuration 2. initParams for each chain should be added in a document.
                chainExecDTO.setInitParams(new HashMap<>());
                chainExecDTO.setJiraId("Auto Executed"); // 此处暂时用 JiraID 表明 是自动执行（scheduled job)

                try {
                    ChainData chainExecutionContext = chainDataService.createChainData(
                            chainExecDTO.getChainId(),
                            chainExecDTO.getEnv(),
                            chainExecDTO.getInitParams(),
                            chainExecDTO.getJiraId()
                    );

                    executeService.doExecute(chainExecutionContext);
                } catch (Exception e) {
                    log.error("execute chain fail", e);
                }

                Thread.sleep(1 * 3 * 1000); // 暂时先不并发， 每个chain的执行间隔 3 秒钟
            }

        } catch (Exception e) {
            log.error("start chain fail", e);
        } finally {
            //unlock
            lockService.release(AUTO_START_CHAIN_LOCK, lockToken);
            log.info("release AUTO_START_CHAIN_LOCK!");
        }

    }


}
