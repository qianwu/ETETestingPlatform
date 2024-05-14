//package org.charlotte.e2ecore.service;
//
//import org.charlotte.e2ecore.ServerApplication;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.charlotte.e2edomain.*;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.mongodb.core.FindAndModifyOptions;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//@SpringBootTest(classes = ServerApplication.class)
//class ExecuteServiceTest {
//
//    @Test
//    public void test_do_execute() {
//        ExecuteService executeService = new ExecuteService();
//
//        String caseId_1 = "caseId1";
//        String caseId_2 = "caseId2";
//        List<Depends> case2Depends = new ArrayList<>();
//        case2Depends.add(new Depends(caseId_1, "$.field11", "field21"));
//        case2Depends.add(new Depends(caseId_1, "$.field12", "field22"));
//        case2Depends.add(new Depends(caseId_1, "$.field13", "field23"));
//        Map<String, List<Depends>> map = new HashMap<>();
//        map.put(caseId_2, case2Depends);
//
//
//        Case c1 = Case.builder().caseNo(caseId_1).build();
//        Case c2 = Case.builder().caseNo(caseId_2).build();
//        List<Case> caseList = new ArrayList<>();
//        caseList.add(c1);
//        caseList.add(c2);
//
//        Chain chain = Chain.builder()
//                .caseList(caseList)
//                .build();
//
//
//        CaseData case1Data = new CaseData();
//        case1Data.setCaseNo(caseId_1);
//        case1Data.setRespBody("{\"field11\":\"s1\",\"field12\":\"s2\",\"field13\":\"s3\"}");
//        List<CaseData> caseDataList = new ArrayList<>();
//        caseDataList.add(case1Data);
//
//        ChainData chainData = ChainData.builder()
//                .latestFinishedCaseIndex(0)
//                .chainSnapshot(chain)
//                .caseDataList(caseDataList)
//                .build();
//
//
//        Map<String, String> params = executeService.params(chainData);
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            Map map1 = objectMapper.readValue("{\"field21\":\"s1\",\"field22\":\"s2\",\"field23\":\"s3\"}", Map.class);
//            Assertions.assertEquals(params, map1);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//
//
//    @Resource
//    private MongoTemplate mongoTemplate;
//
//
//    @Test
//    public void testFindAndModify(){
//        String key = "testFindAndModify";
//        Query query = Query.query(Criteria.where("_id").is(key));
//        String token = "token" + System.currentTimeMillis() + Math.random();
//
//        long expireAt = System.currentTimeMillis() + 100000;
//        Update update = new Update()
//                .setOnInsert("_id", key)
//                .setOnInsert("expireAt", expireAt)
//                .setOnInsert("token", token);
//
//        FindAndModifyOptions options = new FindAndModifyOptions().upsert(true)
//                .returnNew(true);
//        LockDocument doc = mongoTemplate.findAndModify(query, update, options,
//                LockDocument.class);
//        System.out.println(doc);
//    }
//
//}