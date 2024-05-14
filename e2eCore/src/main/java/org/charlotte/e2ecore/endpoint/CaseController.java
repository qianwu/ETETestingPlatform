package org.charlotte.e2ecore.endpoint;

import org.charlotte.e2ecore.exception.IatmException;
import org.charlotte.e2ecore.service.CaseService;
import org.charlotte.e2ecore.service.CollectionService;
import org.charlotte.e2ecore.service.ExecuteService;
import org.charlotte.e2ecore.service.ProjectService;
import org.charlotte.e2ecore.utils.EntityUtils;
import org.charlotte.e2ecore.utils.MongoUtils;
import org.charlotte.e2ecore.utils.TokenUtils;
import com.google.common.collect.Lists;
import org.charlotte.e2edomain.Case;
import org.charlotte.e2edomain.CaseData;
import org.charlotte.e2edomain.ChainData;
import org.charlotte.e2edomain.Project;
import org.charlotte.e2edomain.dto.*;
import org.charlotte.e2edomain.enums.CaseExecStatus;
import org.charlotte.e2edomain.enums.ChainDataStatus;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/case")
public class CaseController {

    @Resource
    private CaseService caseService;

    @Resource
    private CollectionService collectionService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ProjectService projectService;

    @Resource
    private ExecuteService executeService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @PostMapping("/create")
//    @PreAuthorize("hasAnyAuthority('CASE_ADD')")
    public Case createCase(@RequestBody CaseCreateDTO request) throws Exception {
        Map<String, Object> userClaim = TokenUtils.getUserClaim();
        String email = (String) userClaim.get("email");
        String name = (String) userClaim.get("name");

        Query query = new Query(Criteria.where("caseId").is(request.getCaseNo()));
        Case aCase = mongoTemplate.findOne(query, Case.class, MongoUtils.DOC_CASE);
        if (Objects.nonNull(aCase)) {
            throw new Exception("[CREATE FAIL!] case '" + request.getCaseNo() + "' already exists!");
        }

        return collectionService.saveOrUpdate(
                Case.builder()
                .caseNo(request.getCaseNo())
                .projectId(request.getProjectId())
                .workflowId(request.getWorkflowId())
                .input(request.getInput())
                .output(request.getOutput())
                .path(request.getPath())
                .creator(email)
//                .creator(name)
                .comment(request.getComment())
                .isDeleted(Boolean.FALSE)
                .createDateTime(LocalDateTime.now())
                .build()
        );

    }

    @GetMapping("query")
    public CaseDTO getCase(@RequestParam(value = "caseNo", required = false) String caseNo,
                           @RequestParam(value = "id", required = false) String id) throws IatmException {
        Case aCase = caseService.querySingleCase(id, caseNo);
        CaseDTO caseDTO = EntityUtils.copyData(aCase, CaseDTO.class);
        caseDTO.setId(aCase.getId().toHexString());
        if (Objects.nonNull(aCase.getProjectId())) {
            Project project = null;
            try {
                project = collectionService.getDocumentById(aCase.getProjectId(), Project.class);
                caseDTO.setProjectName(project.getName());
            } catch (Exception ex) {
                log.error("Fail to get project name", ex);
            }
        }
        return caseDTO;
    }



    @PostMapping("/update")
//    @PreAuthorize("hasAnyAuthority('CASE_UPDATE')")
    public Case updateCase(@RequestBody CaseUpdateDTO request) throws Exception {
        Query query = new Query(Criteria.where("caseNo").is(request.getCaseNo()).and("_id").is(new ObjectId(request.getId())));
        Case aCase = mongoTemplate.findOne(query, Case.class, MongoUtils.DOC_CASE);
        if (Objects.isNull(aCase)) {
            throw new Exception("[UPDATE FAIL!] case '" + request.getCaseNo() + "' doest not exists!");
        }
        request.copyNotNullValueTo(aCase);
        aCase.setUpdateDateTime(LocalDateTime.now());
        return collectionService.saveOrUpdate(aCase);
    }


    @PostMapping("batchQuery")
    public List<CaseDTO> batchQuery(@RequestBody CaseBatchQueryDTO dto) {
//        String token = httpServletRequest.getHeader("Authorization").split(" ")[1];

        List<Case> caseList = caseService.queryCasesByConditions(dto);

        if (CollectionUtils.isEmpty(caseList)) {
            return Lists.newArrayList();
        }

        List<Project> projectList = projectService.getProjectListByIdList(
                caseList.stream().map(Case::getProjectId).collect(Collectors.toList())
        );

        Map<String, String> projectIdMapName = projectList.stream().collect(
                Collectors.toMap(
                        k -> {
                            return k.getId().toHexString();
                        },
                        Project::getName,
                        (key1, key2) -> key2)
        );

        List<CaseDTO> caseDTOList = new ArrayList<>(caseList.size());
        for (Case item : caseList) {
            CaseDTO caseDTO = EntityUtils.copyData(item, CaseDTO.class);
            caseDTO.setId(item.getId().toHexString());
            caseDTO.setProjectName(projectIdMapName.get(item.getProjectId()));
            caseDTO.setCreateDateTime(item.getCreateDateTime());
            caseDTOList.add(caseDTO);
        }

        return caseDTOList;
    }


    @GetMapping("/list")
    public List<CaseDTO> getList() {
        System.out.println("get all list");

        List<Case> caseList = caseService.queryCasesByConditions(new CaseBatchQueryDTO());

        if (CollectionUtils.isEmpty(caseList)) {
            return Lists.newArrayList();
        }

        List<Project> projectList = projectService.getProjectListByIdList(
                caseList.stream().map(Case::getProjectId).collect(Collectors.toList())
        );

        Map<String, String> projectIdMapName = projectList.stream().collect(
                Collectors.toMap(
                        k -> {
                            return k.getId().toHexString();
                        },
                        Project::getName,
                        (key1, key2) -> key2)
        );

        List<CaseDTO> caseDTOList = new ArrayList<>(caseList.size());
        for (Case item : caseList) {
            CaseDTO caseDTO = EntityUtils.copyData(item, CaseDTO.class);
            caseDTO.setId(item.getId().toHexString());
            caseDTO.setProjectName(projectIdMapName.get(item.getProjectId()));
            caseDTO.setCreateDateTime(item.getCreateDateTime());
            caseDTOList.add(caseDTO);
        }

        return caseDTOList;
    }


    @DeleteMapping("delete")
//    @PreAuthorize("hasAnyAuthority('CASE_DEL')")
    public void deleteCase(@RequestParam(value = "id") String caseId) throws Exception {
        Case aCase = collectionService.getDocumentById(caseId, Case.class);
        if (aCase.getIsDeleted()) {
            return;
        }
        caseService.deleteCase(caseId);
    }


    /**
     * call back api
     * for Test Framework
     * when single case execution complete
     *
     * @param request chainDataId, respBody, currentCaseIndex
     */
    @PostMapping("/exec/complete")
    public void completeCaseExec(@RequestBody CaseExecCompleteDTO request) throws IatmException {
        log.info("===+++===start handle case exec complete logic:");
        log.info(request.toString());

        ChainData chainData = collectionService.getDocumentById(request.getChainDataId(), ChainData.class);
        if (chainData.getLatestFinishedCaseIndex() + 1 != request.getCurrentCaseIndex()
                || request.getCurrentCaseIndex() >= chainData.getChainSnapshot().getCaseList().size()
        ) {
            log.error("current case execution does not match!");
            return;
        }
        chainData.getGlobalData().putAll(request.getRespBody());

        //更新CaseDataList/currentCaseIndex
        Case currentCase = chainData.getChainSnapshot().getCaseList().get(request.getCurrentCaseIndex());
        CaseData executeCompletedCaseData = CaseData.builder()
                .caseNo(currentCase.getCaseNo())
                .createDateTime(LocalDateTime.now())
                .projectId(currentCase.getProjectId())
                .id(currentCase.getId())
                .reqBody(request.getReqBody().toString())
                .respBody(request.getRespBody().toString())
                .log(request.getCaseExecutionLog())
                .status(Objects.isNull(request.getTestExecutionStatus()) ? CaseExecStatus.PASSED.name() : request.getTestExecutionStatus().name())
                .build();
        if (Integer.valueOf(0).equals(request.getCurrentCaseIndex())) {
            chainData.setCaseDataList(Lists.newLinkedList());
        }
        chainData.getCaseDataList().add(executeCompletedCaseData);
        chainData.setLatestFinishedCaseIndex(request.getCurrentCaseIndex());
        if (CaseExecStatus.PASSED.name().equals(executeCompletedCaseData.getStatus())) {
            if (!chainData.hasNextCase()) {
                chainData.setStatus(ChainDataStatus.PASSED.name());
            }
        } else {
            chainData.setStatus(ChainDataStatus.FAILED.name());
        }
        chainData.setUpdateDateTime(LocalDateTime.now());
        collectionService.saveOrUpdate(chainData);

        //是否有待执行测试用例
        if (chainData.hasNextCase() && CaseExecStatus.PASSED.name().equals(executeCompletedCaseData.getStatus())) {
            executeService.doExecute(chainData);
        }
    }


}
