package org.charlotte.e2ecore.endpoint;

import org.charlotte.e2ecore.exception.IatmException;
import org.charlotte.e2ecore.service.CollectionService;
import org.charlotte.e2ecore.service.TestUserDataService;
import org.charlotte.e2ecore.utils.EntityUtils;
import org.charlotte.e2ecore.utils.MongoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.charlotte.e2edomain.Project;
import org.charlotte.e2edomain.TestUserData;
import org.charlotte.e2edomain.dto.TestUserDataBatchCreateDTO;
import org.charlotte.e2edomain.dto.TestUserDataBatchQueryDTO;
import org.charlotte.e2edomain.dto.TestUserDataDTO;
import org.charlotte.e2edomain.dto.TestUserDataUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Slf4j
@RestController
@RequestMapping("/api/testUserData")
public class TestUserDataController {

    public TestUserDataController() {
        this.objectMapper = new ObjectMapper();
    }

    @Resource
    RestTemplate restTemplate;

    ObjectMapper objectMapper;

    @Value("${github.token}")
    String authorization_token;

    @Value("${github.accept.default}")
    String github_accept_header;

    @Resource
    private TestUserDataService testUserDataService;

    @Resource
    private CollectionService collectionService;

    @Resource
    private MongoTemplate mongoTemplate;


    @GetMapping("query")
    public TestUserDataDTO getTestUserDataById(@RequestParam(value = "id", required = true) String id) throws IatmException {
        TestUserData testUserData = testUserDataService.querySingleTestUserData(id);
        TestUserDataDTO testUserDataDTO = EntityUtils.copyData(testUserData, TestUserDataDTO.class);
        testUserDataDTO.setId(testUserData.getId());

        return testUserDataDTO;
    }

    @PostMapping("/create")
    public TestUserData create(@RequestBody TestUserDataBatchCreateDTO dto) {
        TestUserData testUserData = TestUserData.builder()
                .name(dto.getName())
                .channel(dto.getChannel())
                .mobile(dto.getMobile())
                .partyUid(dto.getPartyUid())
                .token(dto.getToken())
                .env(dto.getEnv())
                .createDateTime(LocalDateTime.now())
                .build();

        System.out.println("testUserData");
        System.out.println(testUserData);
        return collectionService.saveOrUpdate(testUserData);
    }

    @PostMapping("/update")
//    @PreAuthorize("hasAnyAuthority('CASE_UPDATE')")
    public TestUserData updateTestUserData(@RequestBody TestUserDataUpdateDTO dto) throws Exception {
        System.out.println("dto:");
        System.out.println(dto);
        TestUserData testUserData = collectionService.getDocumentById(dto.getId(), TestUserData.class);

        if (Objects.isNull(testUserData)) {
            throw new Exception("[UPDATE FAIL!] testUserData '" + dto.getId() + "' doest not exists!");
        }
        testUserData.setEnv(dto.getEnv());
        testUserData.setName(dto.getName());
        testUserData.setChannel(dto.getChannel());
        testUserData.setMobile(dto.getMobile());
        testUserData.setToken(dto.getToken());
        testUserData.setUpdateDateTime(LocalDateTime.now());
        return collectionService.saveOrUpdate(testUserData);
    }

    @DeleteMapping("delete")
    public void delete(@RequestParam("id") String id) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        mongoTemplate.remove(query, Project.class);
    }

    @PostMapping("batchQuery")
    public List<TestUserData> batchQuery(@RequestBody TestUserDataBatchQueryDTO dto) {
        return mongoTemplate.find(new Query().limit(Objects.isNull(dto.getLimit()) ? MongoUtils.DEFAULT_LIMIT_NUM : dto.getLimit()),
                TestUserData.class);
    }

}
