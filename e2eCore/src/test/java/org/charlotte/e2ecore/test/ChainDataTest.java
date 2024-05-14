//package org.charlotte.e2ecore.test;
//
//import org.charlotte.e2ecore.ServerApplication;
//import org.charlotte.e2ecore.service.CollectionService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.Lists;
//import org.charlotte.e2edomain.*;
//import com.jayway.jsonpath.Configuration;
//import com.jayway.jsonpath.Option;
//import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
//import com.jayway.jsonpath.spi.json.JsonProvider;
//import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
//import com.jayway.jsonpath.spi.mapper.MappingProvider;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.Resource;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@SpringBootTest(classes = ServerApplication.class)
//public class ChainDataTest {
//    @Resource
//    private CollectionService collectionService;
//
//    private static final String PROMOTION = "promotion";
//    private static final String DELIVERY = "delivery";
//    private static final String CCS = "ccs";
//

//
//    private static final String STATUS_INVALID = "0";
//
//    private static final Integer PROMOTION_WORKFLOW_ID = 10001;
//    private static final Integer DELIVERY_WORKFLOW_ID = 10002;
//    private static final Integer CCS_WORKFLOW_ID = 10003;
//
//    private static final String PROMOTION_CASE_ID = "c10001";
//    private static final String DELIVERY_CASE_ID = "c10002";
//    private static final String CCS_CASE_ID = "c10003";
//
//    private static final String JSON = "json";
//
//    private static final String TEST_ENGINE_DEMO = "TestEngineDemo";
//
//    @Test
//    public List<Project> createProjects() {
//        Project promotion = Project.builder()
//                .name(PROMOTION)
//                .workflowId(PROMOTION_WORKFLOW_ID)
//                .gitOwner(PROMOTION)
//                .gitRepo(PROMOTION_REPO)
//                .status(STATUS_INVALID)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        Project delivery = Project.builder()
//                .name(DELIVERY)
//                .workflowId(DELIVERY_WORKFLOW_ID)
//                .gitOwner(DELIVERY)
//                .gitRepo(DELIVERY_REPO)
//                .status(STATUS_INVALID)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        Project ccs = Project.builder()
//                .name(CCS)
//                .workflowId(CCS_WORKFLOW_ID)
//                .gitOwner(CCS)
//                .gitRepo(CCS_REPO)
//                .status(STATUS_INVALID)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        promotion = collectionService.saveOrUpdate(promotion);
//        delivery = collectionService.saveOrUpdate(delivery);
//        ccs = collectionService.saveOrUpdate(ccs);
//
//        return Lists.newArrayList(promotion, delivery, ccs);
//    }
//
//    @Test
//    public List<Category> createCategories(List<Project> projectList) {
//        Map<String, Project> projectNameMap = projectList.stream().collect(Collectors.toMap(Project::getName, p -> p));
//
//        Category promotion = Category.builder()
//                .name(PROMOTION)
//                .projectId(projectNameMap.get(PROMOTION).getId().toHexString())
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        Category delivery = Category.builder()
//                .name(DELIVERY)
//                .projectId(projectNameMap.get(DELIVERY).getId().toHexString())
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        Category ccs = Category.builder()
//                .name(CCS)
//                .projectId(projectNameMap.get(CCS).getId().toHexString())
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        promotion = collectionService.saveOrUpdate(promotion);
//        delivery = collectionService.saveOrUpdate(delivery);
//        ccs = collectionService.saveOrUpdate(ccs);
//
//        return Lists.newArrayList(promotion, delivery, ccs);
//    }
//
//    @Test
//    public List<Case> createCases(List<Project> projectList, List<Category> categoryList) {
//        Map<String, Project> projectNameMap = projectList.stream().collect(Collectors.toMap(Project::getName, p -> p));
//        Map<String, Category> categoryNameMap = categoryList.stream().collect(Collectors.toMap(Category::getName, c -> c));
//
//        Case promotionCase = Case.builder()
//                .projectId(projectNameMap.get(PROMOTION).getId().toHexString())
//                .categoryId(categoryNameMap.get(PROMOTION).getId().toHexString())
//                .status(STATUS_INVALID)
//                .workflowId(projectNameMap.get(PROMOTION).getWorkflowId())
//                .caseNo(PROMOTION_CASE_ID)
//                .path(projectNameMap.get(PROMOTION).getGitRepo())
//                .comment(PROMOTION)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        Case deliveryCase = Case.builder()
//                .projectId(projectNameMap.get(DELIVERY).getId().toHexString())
//                .categoryId(projectNameMap.get(DELIVERY).getId().toHexString())
//                .status(STATUS_INVALID)
//                .workflowId(projectNameMap.get(DELIVERY).getWorkflowId())
//                .caseNo(DELIVERY_CASE_ID)
//                .path(projectNameMap.get(DELIVERY).getGitRepo())
//                .comment(DELIVERY)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        Case ccsCase = Case.builder()
//                .projectId(projectNameMap.get(CCS).getId().toHexString())
//                .categoryId(projectNameMap.get(CCS).getId().toHexString())
//                .status(STATUS_INVALID)
//                .workflowId(projectNameMap.get(CCS).getWorkflowId())
//                .caseNo(CCS_CASE_ID)
//                .path(projectNameMap.get(CCS).getGitRepo())
//                .comment(CCS)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        promotionCase = collectionService.saveOrUpdate(promotionCase);
//        deliveryCase = collectionService.saveOrUpdate(deliveryCase);
//        ccsCase = collectionService.saveOrUpdate(ccsCase);
//
//        Category promotionCategory = categoryNameMap.get(PROMOTION);
//        promotionCategory.setCaseList(Lists.newArrayList(promotionCase));
//
//        Category deliveryCategory = categoryNameMap.get(DELIVERY);
//        deliveryCategory.setCaseList(Lists.newArrayList(promotionCase));
//
//        Category ccsCategory = categoryNameMap.get(CCS);
//        ccsCategory.setCaseList(Lists.newArrayList(promotionCase));
//
//        collectionService.saveOrUpdate(promotionCategory);
//        collectionService.saveOrUpdate(deliveryCategory);
//        collectionService.saveOrUpdate(ccsCategory);
//
//        return Lists.newArrayList(promotionCase, deliveryCase, ccsCase);
//    }
//
//    @Test
//    public Chain createChain(List<Case> caseList) {
//        Depends promotionDepends1 = Depends.builder()
//                .sourceCaseId(PROMOTION_CASE_ID)
//                .sourceKey("$.couponId")
//                .targetKey("${couponId}")
//                .build();
//        Depends promotionDepends2 = Depends.builder()
//                .sourceCaseId(PROMOTION_CASE_ID)
//                .sourceKey("$.couponPromotionCode")
//                .targetKey("${couponPromotionCode}")
//                .build();
//        Depends promotionDepends3 = Depends.builder()
//                .sourceCaseId(PROMOTION_CASE_ID)
//                .sourceKey("$.customerId")
//                .targetKey("${customerId}")
//                .build();
//
//        Depends deliveryDepends1 = Depends.builder()
//                .sourceCaseId(DELIVERY_CASE_ID)
//                .sourceKey("$.delivery_arrangement_id")
//                .targetKey("${delivery_arrangement_id}")
//                .build();
//        Depends deliveryDepends2 = Depends.builder()
//                .sourceCaseId(DELIVERY_CASE_ID)
//                .sourceKey("$.solution_id")
//                .targetKey("${solution_id}")
//                .build();
//        Depends deliveryDepends3 = Depends.builder()
//                .sourceCaseId(DELIVERY_CASE_ID)
//                .sourceKey("$.time_window")
//                .targetKey("${time_window}")
//                .build();
//        Depends deliveryDepends4 = Depends.builder()
//                .sourceCaseId(DELIVERY_CASE_ID)
//                .sourceKey("$.delivery_id")
//                .targetKey("${delivery_id}")
//                .build();
//
//        Map<String, List<Depends>> depList = new HashMap<>();
//        List<Depends> ccsDepends = Lists.newArrayList(promotionDepends1, promotionDepends2, promotionDepends3,
//                deliveryDepends1, deliveryDepends2, deliveryDepends3, deliveryDepends4);
//
//        depList.put(CCS_CASE_ID, ccsDepends);
//
//        Chain chain = Chain.builder()
//                .name(TEST_ENGINE_DEMO)
//                .caseList(caseList)
////                .depList(depList)
//                .status(STATUS_INVALID)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        chain = collectionService.saveOrUpdate(chain);
//        return chain;
//    }
//
//    @Test
//    public List<CaseData> createCaseDataList(List<Case> caseList) {
//        Map<String, Case> caseNameMap = caseList.stream().collect(Collectors.toMap(Case::getCaseNo, c -> c));
//
//        CaseData promotionCaseData = CaseData.builder()
//                .caseNo(caseNameMap.get(PROMOTION_CASE_ID).getCaseNo())
//                .projectId(caseNameMap.get(PROMOTION_CASE_ID).getProjectId())
//                .respBody("{\"couponId\": \"299922639708639232\",\"couponPromotionCode\": \"LUCY10-5\",\"customerId\": \"IZQA-47990fe6-e0a7-4f08-a3ac-190f8f47b9d0\"}")
//                .status(STATUS_INVALID)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        CaseData deliveryCaseData = CaseData.builder()
//                .caseNo(caseNameMap.get(DELIVERY_CASE_ID).getCaseNo())
//                .projectId(caseNameMap.get(DELIVERY_CASE_ID).getProjectId())
//                .respBody("{\"delivery_arrangement_id\": \"20211105070616936230557\",\"solution_id\": \"HD~2~STANDARD\",\"delivery_id\": \"HD~~~5\",\"time_window\": {\"id\": \"ff999672-5b6d-451f-8580-105200d4eff4\",\"startDateTime\": \"2021-11-07T08:00:00\",\"endDateTime\": \"2021-11-07T17:00:00\"}}")
//                .status(STATUS_INVALID)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        CaseData ccsCaseData = CaseData.builder()
//                .caseNo(caseNameMap.get(CCS_CASE_ID).getCaseNo())
//                .projectId(caseNameMap.get(CCS_CASE_ID).getProjectId())
//                .reqBody("{\"delivery_arrangement_id\": \"${delivery_arrangement_id}\",\"solution_id\": \"${solution_id}\",\"delivery_id\": \"${delivery_id}\",\"time_window\": \"${time_window}\",\"coupon_id\": \"${couponId}\",\"coupon_code\": \"${couponPromotionCode}\",\"customerId\": \"${customerId}\"}")
//                .status(STATUS_INVALID)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//
//        promotionCaseData = collectionService.saveOrUpdate(promotionCaseData);
//        deliveryCaseData = collectionService.saveOrUpdate(deliveryCaseData);
//        ccsCaseData = collectionService.saveOrUpdate(ccsCaseData);
//
//        return Lists.newArrayList(promotionCaseData, deliveryCaseData, ccsCaseData);
//    }
//
//    @Test
//    public ChainData createChainData(List<CaseData> caseDataList, Chain chain) {
//        ChainData chainData = ChainData.builder()
//                .chainSnapshot(chain)
//                .caseDataList(caseDataList)
//                .status(STATUS_INVALID)
//                .currCaseId(CCS_CASE_ID)
//                .createDateTime(LocalDateTime.now())
//                .updateDateTime(LocalDateTime.now())
//                .build();
//        chainData = collectionService.saveOrUpdate(chainData);
//        return chainData;
//    }
//
//    @Test
//    public String executeChain(ChainData chainData) throws Exception {
//        Chain chain = collectionService.getDocumentById(chainData.getChainSnapshot().getId().toString(), Chain.class);
//
//        JsonProvider jsonProvider = new JacksonJsonProvider();
//        MappingProvider mappingProvider = new JacksonMappingProvider();
//
//        Configuration conf = Configuration.defaultConfiguration()
//                .jsonProvider(jsonProvider)
//                .mappingProvider(mappingProvider)
//                .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
//
//        List<CaseData> caseDataList = chainData.getCaseDataList();
//        Map<String, CaseData> caseMap = caseDataList.stream().collect(Collectors.toMap(CaseData::getCaseNo, c -> c));
//
////        for (Map.Entry<String, List<Depends>> entry : chain.getDepList().entrySet()) {
////            String caseId = entry.getKey();
////            for (Depends depends : entry.getValue()) {
////                String depCaseId = depends.getSourceCaseId();
////                String sourceKey = depends.getSourceKey(); // $.xxx
////                String targetKey = depends.getTargetKey(); // ${yyy}
////
////                CaseData depCaseData = caseMap.get(depCaseId);
////                DocumentContext context = JsonPath.using(conf).parse(depCaseData.getRespBody());
////                Object val = context.read(sourceKey);
////
////                CaseData targetCaseData = caseMap.get(caseId);
////                String reqBody = targetCaseData.getReqBody();
////                targetCaseData.setReqBody(reqBody.replace(targetKey, val.toString()));
////            }
////        }
//
//        CaseData caseData = caseMap.get(CCS_CASE_ID);
//        caseData.setRespBody("{\"checkout_id\":\"20211105070616936230557\",\"order_id\":12345678}");
//
//        collectionService.saveOrUpdate(caseData);
//        collectionService.saveOrUpdate(chainData);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.writeValueAsString(chainData);
//    }
//
//
//    @Test
//    public void removeAllCollection() {
//        collectionService.clearAllCollection();
//    }
//
//
//    @Test
//    public void totalProcess() throws Exception {
//        List<Project> projectList = createProjects();
//        List<Category> categoryList = createCategories(projectList);
//        List<Case> caseList = createCases(projectList, categoryList);
//
//        Chain chain = createChain(caseList);
////        List<CaseData> caseDataList = createCaseDataList(caseList);
//
////        ChainData chainData = createChainData(caseDataList, chain);
////        String json = executeChain(chainData);
////        System.out.println(json);
//    }
//
//
//    @Test
//    public void testData() throws Exception {
//        ChainData chainData = collectionService.getDocumentById("6189e78436c0ae18b9c40a13", ChainData.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        System.out.println(objectMapper.writeValueAsString(chainData));
//    }
//}
