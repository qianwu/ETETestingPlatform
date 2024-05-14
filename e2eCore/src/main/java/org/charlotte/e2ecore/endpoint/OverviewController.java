package org.charlotte.e2ecore.endpoint;

import org.charlotte.e2ecore.service.*;
import com.google.common.collect.Maps;
import org.charlotte.e2edomain.response.CountResourcesResponse;
import org.charlotte.e2edomain.response.LatestExecutionDTO;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/overview")
public class OverviewController {

    @Resource
    private ProjectService projectService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private CaseService caseService;

    @Resource
    private ChainService chainService;

    @Resource
    private ChainDataService chainDataService;


    @GetMapping("/countResources")
    public CountResourcesResponse countResources() {
        return CountResourcesResponse.builder()
                .projectCount(projectService.countAll())
                .caseCount(caseService.countAll())
                .categoryCount(categoryService.countAll())
                .chainCount(chainService.countAll())
                .dataToolCount(chainService.countAllDataTools())
                .build();
    }

    @GetMapping("listLatestExecution")
    public List<LatestExecutionDTO> listLatestExecution() {
        return chainDataService.getLatestChainDataForExecutedChain();
    }

    @GetMapping("getOvertimeChainDataId")
    public List<String> getOvertimeChainDataId() {
        List<ObjectId> overtimeChainDataId = chainDataService.getOvertimeChainDataId();
        return overtimeChainDataId.stream().map(ObjectId::toHexString).collect(Collectors.toList());
    }

    @PostMapping("handleSingleOvertimeChainData")
    public void handleSingleOvertimeChainData(@RequestParam String objectId) {
        chainDataService.handleSingleOvertimeChainData(new ObjectId(objectId));
    }

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("restTemplate")
    public boolean restTemplateTest(){
        CountResourcesResponse forObject = restTemplate.getForObject("http://localhost:8080/api/overview/headerTest", CountResourcesResponse.class, Maps.newHashMap());
        return true;
    }

    @Resource
    private HttpServletRequest httpServletRequest;

    @GetMapping("headerTest")
    public CountResourcesResponse headerTest(){

        String token = httpServletRequest.getHeader("token");
        log.info(token);
        return CountResourcesResponse.builder()
                .projectCount(projectService.countAll())
                .caseCount(caseService.countAll())
                .categoryCount(categoryService.countAll())
                .chainCount(chainService.countAll())
                .build();
    }


}
