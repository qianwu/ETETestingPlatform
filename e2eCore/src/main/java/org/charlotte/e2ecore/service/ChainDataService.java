package org.charlotte.e2ecore.service;

import org.charlotte.e2ecore.utils.DateUtil;
import org.charlotte.e2ecore.utils.MongoUtils;
import com.google.common.collect.Lists;
import org.charlotte.e2edomain.*;
import org.charlotte.e2edomain.enums.CaseExecStatus;
import org.charlotte.e2edomain.enums.ChainDataStatus;
import org.charlotte.e2edomain.response.LatestExecutionDTO;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ChainDataService {

    private final static String DESC_SEPARATOR = ".<br>";

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ChainService chainService;

    public ChainData createChainData(String chainId,
                                     String env,
                                     Map<String, String> initParams,
                                     String jiraId) {
        Chain chainToExecute = mongoTemplate.findById(chainId, Chain.class);
        if (null == chainToExecute) {
            throw new RuntimeException();
        }

        Map<String, String> params = new HashMap<>();
        params.putAll(initParams);

        ChainData executionContext = ChainData.builder()
                .chainSnapshot(chainToExecute)
                .env(env)
                .globalData(params)
                .latestFinishedCaseIndex(-1)
                .createDateTime(LocalDateTime.now())
                .updateDateTime(LocalDateTime.now())
                .jiraId(jiraId)
                .status(ChainDataStatus.EXECUTING.name())
                .build();
        mongoTemplate.save(executionContext);
        return executionContext;
    }

    public List<LatestExecutionDTO> getLatestChainDataForExecutedChain() {
        List<Document> chainDocList = this.getLatestChainDataForExecutedChainWithDoc();
        if (CollectionUtils.isEmpty(chainDocList)) {
            return Lists.newArrayList();
        }

        List<Chain> allUndeletedChainList = chainService.getAllUndeletedChainList();
        Map<ObjectId, Chain> idChainMap = allUndeletedChainList.stream().collect(Collectors.toMap(Chain::getId, v -> v));
        List<LatestExecutionDTO> result = new ArrayList<>();

        for (Document document : chainDocList) {
            Document latestExecution = document.get("latestExecution", Document.class);
            Document chainSnapshot = latestExecution.get("chainSnapshot", Document.class);
            ObjectId chainObjectId = (ObjectId) chainSnapshot.get("_id");

            if (!idChainMap.containsKey(chainObjectId)) {
                continue;
            }

            result.add(this.convertDocument2LatestExecutionDTO(document, idChainMap.get(chainObjectId)));
        }

        return result;
    }

    /**
     * 解析Document
     *
     * @param document {@link ChainDataService#getLatestChainDataForExecutedChainWithDoc()} 方法输出的列表中的元素
     * @return 解析后的 {@link LatestExecutionDTO}
     */
    public LatestExecutionDTO convertDocument2LatestExecutionDTO(Document document, Chain chain) {
        Document latestExecution = document.get("latestExecution", Document.class);
        Document chainSnapshot = latestExecution.get("chainSnapshot", Document.class);
        ObjectId chainObjectId = (ObjectId) chainSnapshot.get("_id");

        ObjectId executionId = latestExecution.get("_id", ObjectId.class);
        String status = latestExecution.get("status", String.class);
        ArrayList<Document> caseList = chainSnapshot.get("caseList", ArrayList.class);
        StringBuilder descStringBuilder = new StringBuilder();
        Date latestExecutionTime = document.get("latestExecutionTime", Date.class);

        for (int i = 0; i < caseList.size(); i++) {
            Document caseDoc = caseList.get(i);
            String comment = caseDoc.get("comment", String.class);
            descStringBuilder.append("case-");
            descStringBuilder.append(i + 1);
            descStringBuilder.append(": ");
            descStringBuilder.append(comment);
            if (i < caseList.size() - 1) {
                descStringBuilder.append(DESC_SEPARATOR);
            }
        }

        return LatestExecutionDTO.builder()
                .executionTime(DateUtil.convertFrom(latestExecutionTime))
                .chainName(chain.getName())
                .chainId(chainObjectId.toHexString())
                .testCaseCount(caseList.size())
                .status(status)
                .desc(descStringBuilder.toString())
                .executionId(executionId.toHexString())
                .build();
    }

    /**
     * 查询有执行记录的chain信息 及其最近一次执行信息
     *
     * @return {@link Document}列表 元素由后续{@link ChainDataService#convertDocument2LatestExecutionDTO(org.bson.Document)}方法解析
     */
    public List<Document> getLatestChainDataForExecutedChainWithDoc() {
        // 按照 chain执行的启动时间 算该chain最近的一次执行
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection(MongoUtils.DOC_CHAIN_DATA)
                .aggregate(Arrays.asList(
                        Aggregates.sort(new Document().append("createDateTime", -1)),
                        Aggregates.group(
                                new Document("_id", "$chainSnapshot._id"),
                                Arrays.asList(
                                        // 取分组第一条数据
                                        Accumulators.first("latestExecution", "$$ROOT"),
                                        Accumulators.max("latestExecutionTime", "$createDateTime"))
                        ),
                        Aggregates.sort(new Document().append("latestExecutionTime", -1))
                ));
        return Lists.newArrayList(aggregateIterable);
    }


    /**
     * 查询未在5分钟内回调更新
     *
     * @return
     */
    public List<ObjectId> getOvertimeChainDataId() {
        List<AggregationOperation> operations = new ArrayList<>(2);
        operations.add(Aggregation.match(Criteria.where("status").is(ChainDataStatus.EXECUTING.name())
                .and("updateDateTime").lt(LocalDateTime.now().minusMinutes(5))));
        operations.add(Aggregation.project("_id"));
        AggregationResults<ChainData> documents = mongoTemplate.aggregate(
                Aggregation.newAggregation(operations), MongoUtils.DOC_CHAIN_DATA, ChainData.class
        );

        return Lists.newArrayList(documents)
                .stream()
                .map(BaseModel::getId)
                .collect(Collectors.toList());
    }

    //todo transaction
    public void handleSingleOvertimeChainData(ObjectId chainDataId) {
        ChainData chainData = mongoTemplate.findById(chainDataId, ChainData.class);
        //todo 幂等
        int currentCaseIndex = chainData.getLatestFinishedCaseIndex() + 1;
        //更新CaseDataList
        Case currentCase = chainData.getChainSnapshot().getCaseList().get(currentCaseIndex);
        CaseData executeOvertimeCaseData = CaseData.builder()
                .caseNo(currentCase.getCaseNo())
                .createDateTime(LocalDateTime.now())
                .projectId(currentCase.getProjectId())
                .id(currentCase.getId())
                .log(CaseExecStatus.TIMEOUT.getDesc())
                .status(CaseExecStatus.TIMEOUT.name())
                .build();
        if (chainData.getLatestFinishedCaseIndex() == -1) {
            chainData.setCaseDataList(Lists.newLinkedList());
        }
        //状态置为failed
        chainData.getCaseDataList().add(executeOvertimeCaseData);
        chainData.setStatus(ChainDataStatus.FAILED.name());
        chainData.setUpdateDateTime(LocalDateTime.now());
        mongoTemplate.save(chainData);
    }

    /**
     * 正式处理未及时回调的chain运行时数据
     */
    public void doScheduleOvertimeChainDataCallback() {

        List<ObjectId> overtimeChainDataIdList = this.getOvertimeChainDataId();
        log.info("doScheduleOvertimeChainDataCallback - overtimeChainDataIdList={}", overtimeChainDataIdList.toString());

        for (ObjectId overtimeChainDataId : overtimeChainDataIdList) {
            this.handleSingleOvertimeChainData(overtimeChainDataId);
        }
    }


}
