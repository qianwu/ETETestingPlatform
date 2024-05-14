package org.charlotte.e2ecore.service;

import org.charlotte.e2ecore.utils.MongoUtils;
import org.charlotte.e2edomain.Chain;
import org.charlotte.e2edomain.dto.ChainBatchQueryDTO;
import org.charlotte.e2edomain.enums.ChainType;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author ：charlotte
 * @date ：Created in 7/12/21 4:42 PM
 * @description ：
 */
@Slf4j
@Service
public class ChainService {

    @Resource
    private MongoTemplate mongoTemplate;


    public List<Chain> queryChainByConditions(ChainBatchQueryDTO dto) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        
        if (ChainType.DATA_TOOL.name().equals(dto.getType())) {
            // 只有显示指定 type=ChainType.DATA_TOOL 才设置过滤
            query.addCriteria(Criteria.where("type").is(ChainType.DATA_TOOL.name()));
        } else if (ChainType.CASE_CHAIN.name().equals(dto.getType())) {
            // 指定type=ChainType.CASE_CHAIN的情况
            query.addCriteria(Criteria.where("type").is(ChainType.CASE_CHAIN.name()));
        }

        query.with(Sort.by(Sort.Order.desc("createDateTime")));
        //  chainList 暂时设置默认返回数量为1000
        query.limit(Objects.isNull(dto.getLimit()) ? MongoUtils.DEFAULT_LIMIT_NUM : dto.getLimit());
        return mongoTemplate.find(query, Chain.class);
    }

    public void deleteChain(String chainId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(chainId)));
        Update update = new Update();
        update.set("isDeleted", Boolean.TRUE);
        mongoTemplate.updateMulti(query, update, Chain.class, MongoUtils.DOC_CHAIN);
    }

    public Long countAll() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        return mongoTemplate.count(query, MongoUtils.DOC_CHAIN);
    }

    public Long countAllDataTools() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        query.addCriteria(Criteria.where("type").is(ChainType.DATA_TOOL.name()));
        return mongoTemplate.count(query, MongoUtils.DOC_CHAIN);
    }

    public List<Chain> getAllUndeletedChainList() {
        List<AggregationOperation> operations = new ArrayList<>(2);
        operations.add(Aggregation.match(Criteria.where("isDeleted").is(Boolean.FALSE)));
        operations.add(Aggregation.project("_id", "name"));
        AggregationResults<Chain> documents = mongoTemplate.aggregate(
                Aggregation.newAggregation(operations), MongoUtils.DOC_CHAIN, Chain.class
        );
        List<Chain> result = new ArrayList<>();
        for (Chain chain : documents) {
            result.add(chain);
        }
        return result;
    }

}
