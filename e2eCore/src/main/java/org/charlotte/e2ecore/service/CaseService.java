package org.charlotte.e2ecore.service;

import org.charlotte.e2ecore.exception.BizError;
import org.charlotte.e2ecore.exception.IatmException;
import org.charlotte.e2ecore.utils.MongoUtils;
import com.google.common.collect.Sets;
import org.charlotte.e2edomain.Case;
import org.charlotte.e2edomain.dto.CaseBatchQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CaseService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private CollectionService collectionService;

    public List<Case> queryCasesByPrimaryKeys(List<String> caseIds) {
        if (CollectionUtils.isEmpty(caseIds)) {
            return new ArrayList<>();
        }
        List<Case> resultList = new ArrayList<>(caseIds.size());
        Map<String, Case> idMapCase = this.getCaseMapBy(caseIds);
        for (String caseId : caseIds) {
            resultList.add(idMapCase.get(caseId));
        }
        return resultList;
    }

    public Pair<List<Case>, List<String>> queryCasesWithInvalidByPrimaryKeys(List<String> caseIds) {
        if (CollectionUtils.isEmpty(caseIds)) {
            return new ImmutablePair<>(new ArrayList<>(0), new ArrayList<>(0));
        }

        List<Case> caseList = new ArrayList<>();
        List<String> invalidCaseIdList = new ArrayList<>();
        Map<String, Case> idMapCase = this.getCaseMapBy(caseIds);
        for (String caseId : caseIds) {
            if (idMapCase.get(caseId) == null) {
                invalidCaseIdList.add(caseId);
            } else {
                caseList.add(idMapCase.get(caseId));
            }
        }
        return new ImmutablePair<>(caseList, invalidCaseIdList);
    }

    public Set<Case> queryCaseSetByPrimaryKeys(List<String> caseIds) {
        if (CollectionUtils.isEmpty(caseIds)) {
            return new HashSet<>();
        }
        Query query = new Query(
                Criteria.where("_id").in(
                        caseIds.stream()
                                .map(ObjectId::new)
                                .collect(Collectors.toList())
                )
        );
        return Sets.newHashSet(mongoTemplate.find(query, Case.class));
    }

    public Map<String, Case> getCaseMapBy(List<String> caseIds) {
        if (CollectionUtils.isEmpty(caseIds)) {
            return new HashMap<>();
        }
        Query query = new Query(
                Criteria.where("_id").in(
                        caseIds.stream()
                                .map(ObjectId::new)
                                .collect(Collectors.toList())
                )
        );
        List<Case> caseList = mongoTemplate.find(query, Case.class);
        return caseList.stream().collect(Collectors.toMap(k -> {
            return k.getId().toHexString();
        }, Case -> Case));
    }

    public List<Case> queryCasesByConditions(CaseBatchQueryDTO dto) {
        Query query = new Query();
        if (!StringUtils.isEmpty(dto.getFuzzySearchKey())) {
            query.addCriteria(
                    new Criteria().orOperator(
                            Criteria.where("caseNo").regex(MongoUtils.buildFuzzySearchRegex(dto.getFuzzySearchKey())),
                            Criteria.where("comment").regex(MongoUtils.buildFuzzySearchRegex(dto.getFuzzySearchKey()))
                    )
            );
        }
        query.addCriteria(Criteria.where("isDeleted").is(false));
        query.with(Sort.by(Sort.Order.desc("createDateTime")));
        query.limit(Objects.isNull(dto.getLimit()) ? MongoUtils.DEFAULT_LIMIT_NUM : dto.getLimit());
        return mongoTemplate.find(query, Case.class);
    }


    /**
     * return one certain case info according to inputs
     *
     * @param id     ObjectId in mongoDB
     * @param caseNo caseNo
     * @return org.charlotte.e2edomain.Case
     */
    public Case querySingleCase(String id, String caseNo) throws IatmException {
        //直接根据主键查询
        if (!StringUtils.isEmpty(id)) {
            return collectionService.getDocumentById(id, Case.class);
        }

        Query query = new Query(Criteria.where("caseNo").is(caseNo));
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<Case> cases = mongoTemplate.find(query, Case.class, MongoUtils.DOC_CASE);
        if (CollectionUtils.isEmpty(cases)) {
            throw new IatmException(BizError.NO_SUCH_DATA);
        }
        if (cases.size() > 1) {
            throw new IatmException(BizError.MORE_THAN_ONE_RECORD_FOUND);
        }
        return cases.get(0);

    }

    public void deleteCase(String caseId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(caseId)));
        Update update = new Update();
        update.set("isDeleted", Boolean.TRUE);
        mongoTemplate.updateMulti(query, update, Case.class, MongoUtils.DOC_CASE);
    }

    public Long countAll() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        return mongoTemplate.count(query, MongoUtils.DOC_CASE);
    }
}
