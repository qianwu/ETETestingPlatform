package org.charlotte.e2ecore.endpoint;

import org.charlotte.e2ecore.exception.IatmException;
import org.charlotte.e2ecore.service.CollectionService;
import org.charlotte.e2ecore.utils.MongoUtils;
import org.charlotte.e2edomain.CaseData;
import org.charlotte.e2edomain.ChainData;
import org.charlotte.e2edomain.dto.ChainDataBatchQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.util.BsonUtils;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSOutput;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author ：charlotte
 * @date ：Created in 16/12/21 3:28 PM
 */
@RestController
@Slf4j
@RequestMapping("/api/chainData")
public class ChainDataController {
    @Resource
    private CollectionService collectionService;

    @Resource
    private MongoTemplate mongoTemplate;

    @GetMapping("query")
    public ChainData singleQuery(@RequestParam(value = "id") String id) throws IatmException {
        ChainData chainData = collectionService.getDocumentById(id, ChainData.class);

        // 默认不返回 logContent
        if (chainData.getCaseDataList() != null) {
            chainData.getCaseDataList().forEach(caseData -> caseData.setLog(""));
        }

        return chainData;
    }

    @GetMapping("/getLogContent")
    public String getLogContent(@RequestParam(value = "exec_id") String exec_id,
                                @RequestParam(value = "case_id") String case_id,
                                @RequestParam(value = "index") String index
                                ) {
        String logContent = "No Log Data yet ...";

        try {
            ChainData chainData = collectionService.getDocumentById(exec_id, ChainData.class);

            List<CaseData> caseDataList = chainData.getCaseDataList();

            for (int i = 0; i < caseDataList.size(); i++) {
                // index 表示该case在一个chain的索引（有时 一个chain中会包含相同的case）
                if (new ObjectId(case_id).equals(caseDataList.get(i).getId()) &&
                        String.valueOf(i).equals(index) ) {
                    CaseData caseData = caseDataList.get(i);
                    logContent = caseData.getLog();
                    break;
                }
            }
        } catch (Exception e) {
            log.error("getLogContent Error with exec_id: " + exec_id + " & case_id: " + case_id);
            log.error(e.getMessage());
        }

        return logContent;
    }

    @PostMapping("batchQuery")
    public List<ChainData> batchQuery(@RequestBody ChainDataBatchQueryDTO dto) {
        Query query = new Query(Criteria.where("chainSnapshot.id").is(dto.getChainId()));
        query.with(Sort.by(Sort.Order.desc("createDateTime")));
        query.limit(Objects.isNull(dto.getLimit()) ? MongoUtils.DEFAULT_LIMIT_NUM_FOR_EXECUTION_HISTORY : dto.getLimit());

        List<ChainData> chainDataList = mongoTemplate.find(query, ChainData.class);

        //默认不返回 logContent
        for (ChainData chainData : chainDataList) {
            if (chainData.getCaseDataList() != null) {
                chainData.getCaseDataList().forEach(caseData -> caseData.setLog(""));
            }
        }

        return chainDataList;
    }

    @PostMapping("uploadLog")
    public void stringTest(@RequestParam String chainDataId, @RequestParam Integer currentCaseIndex, @RequestBody String request) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(chainDataId)));
        Update update = new Update();
        update.set("caseDataList." + currentCaseIndex + ".log", request);
        mongoTemplate.updateFirst(query, update, ChainData.class, MongoUtils.DOC_CHAIN_DATA);
    }
}
