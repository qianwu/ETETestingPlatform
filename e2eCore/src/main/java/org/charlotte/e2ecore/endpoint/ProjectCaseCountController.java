package org.charlotte.e2ecore.endpoint;

import org.charlotte.e2ecore.exception.IatmException;
import org.charlotte.e2ecore.remote.github.action.WorkflowClient;
import org.charlotte.e2ecore.service.CollectionService;
import org.charlotte.e2ecore.service.ProjectService;
import org.charlotte.e2ecore.utils.MongoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.charlotte.e2edomain.Project;
import org.charlotte.e2edomain.ProjectCaseCount;
import org.charlotte.e2edomain.dto.*;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/api/projectCaseCount")
public class ProjectCaseCountController {

    public ProjectCaseCountController() {
        this.objectMapper = new ObjectMapper();
    }

    ObjectMapper objectMapper;

    @Resource
    private CollectionService collectionService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private WorkflowClient workflowClient;

    @Resource
    private ProjectService projectService;

    @PostMapping("/create")
    public ProjectCaseCount create(@RequestBody ProjectCaseCountDTO dto) {
        ProjectCaseCount projectCaseCount = ProjectCaseCount.builder()
                .projectId(dto.getProjectId())
                .caseCount(dto.getCaseCount())
                .isDeleted(Boolean.FALSE)
                .createDateTime(LocalDateTime.now())
                .build();
        return collectionService.saveOrUpdate(projectCaseCount);
    }

    //    @PostMapping("/update")
//    public Project update(@RequestBody ProjectUpdateDTO dto) throws Exception {
//        Map<String, Object> userClaim = TokenUtils.getUserClaim();
//        String email = (String) userClaim.get("email");
//        String name = (String) userClaim.get("name");
//
//        Query query = new Query(Criteria.where("_id").is(new ObjectId(dto.getId())));
//        Project project = mongoTemplate.findOne(query, Project.class, MongoUtils.DOC_PROJECT);
//        if (Objects.isNull(project)) {
//            throw new Exception("[UPDATE FAIL!] case '" + dto.getId() + "' doest not exists!");
//        }
//        project.setName(dto.getName());
//        project.setGitPath(dto.getGitPath());
//        project.setDescription(dto.getDescription());
//        project.setUpdateDateTime(LocalDateTime.now());
//
//        return collectionService.saveOrUpdate(project);
//    }
//
//
//    @GetMapping("/query")
//    public ProjectDTO getProject(@RequestParam(value = "name", required = false) String name,
//                                 @RequestParam(value = "id", required = false) String id) throws IatmException {
//        Project project = projectService.querySingleProject(id, name);
//        System.out.println("project");
//        System.out.println(project);
////        ProjectDTO projectDTO = EntityUtils.copyData(project, ProjectDTO.class);
//        return ProjectDTO.builder()
//                .id(project.getId().toHexString())
//                .name(project.getName())
//                .gitPath(project.getGitPath())
//                .gitOwner(project.getGitOwner())
//                .description(project.getDescription())
//                .creator(project.getCreator())
//                .build();
//    }
//
//
//    @DeleteMapping("delete")
//    public void delete(@RequestParam("id") String id) throws Exception {
//        Project project = collectionService.getDocumentById(id, Project.class);
//        if (project.getIsDeleted()) {
//            return;
//        }
//        projectService.deleteProject(id);
//    }
//

//    @PostMapping("/batchQuery2")
//    public List<ProjectCaseCountListDTO> batchQuery2(@RequestBody ProjectBatchQueryDTO dto) {
//        List<ProjectCaseCount> projectCaseCountList = mongoTemplate.find(
//                new Query().addCriteria(Criteria.where("isDeleted").is(false)).limit(Objects.isNull(dto.getLimit()) ? MongoUtils.DEFAULT_LIMIT_NUM : dto.getLimit()),
//                ProjectCaseCount.class);
//        log.info("projectCaseCountList:");
//        log.info(projectCaseCountList.toString());
//
//        List<Project> projectList = mongoTemplate.find(
//                new Query().addCriteria(Criteria.where("isDeleted").is(false)).limit(Objects.isNull(dto.getLimit()) ? MongoUtils.DEFAULT_LIMIT_NUM : dto.getLimit()),
//                Project.class);
//
//        log.info("projectList:");
//        log.info(projectList.toString());
//
//        List<ProjectCaseCountDTO> projectCaseCountDTOList = new ArrayList<>();
//
//        for (ProjectCaseCount projectCaseCount : projectCaseCountList) {
//            log.info("projectCaseCount: ");
//            log.info(projectCaseCount.toString());
//            ProjectCaseCountDTO projectCaseCountDTO = new ProjectCaseCountDTO();
//            projectCaseCountDTO.setId(projectCaseCount.getId());
//            projectCaseCountDTO.setProjectId(projectCaseCount.getProjectId());
//            projectCaseCountDTO.setCreateDateTime(projectCaseCount.getCreateDateTime());
//
//            for (Project project : projectList) {
////                log.info("project:");
////                log.info(project.toString());
////                log.info("projectCaseCount.getProjectId():");
////                log.info(projectCaseCount.getProjectId());
////                log.info("project.getId():");
////                log.info(project.getId().toString());
//
//                if (projectCaseCount.getProjectId().trim().length() > 1) {
//                    if (new ObjectId(String.valueOf(projectCaseCount.getProjectId())).equals(project.getId())) {
//                        projectCaseCountDTO.setProjectName(project.getName());
//                        projectCaseCountDTO.setGitPath(project.getGitPath());
//                        break;
//                    }
//                }
//            }
//
//            projectCaseCountDTO.setCaseCount(projectCaseCount.getCaseCount());
//            projectCaseCountDTOList.add(projectCaseCountDTO);
//        }
//
//        List<ProjectCaseCountListDTO> res = new ArrayList<>();
//
//        for (Project project : projectList) {
//            ProjectCaseCountListDTO projectCaseCountListDTO = new ProjectCaseCountListDTO();
//            projectCaseCountListDTO.setProjectId(String.valueOf(project.getId()));
//            projectCaseCountListDTO.setProjectName(project.getName());
//            projectCaseCountListDTO.setGitPath(project.getGitPath());
//
//            ArrayList<CaseCountInfoDTO> caseCountInfoDTOArrayList = new ArrayList<>();
//            projectCaseCountListDTO.setCaseCountList(caseCountInfoDTOArrayList);
//
//            int count = 1;
//            for (int i = projectCaseCountDTOList.size() - 1; i > -1; i--) {
//                if (projectCaseCountDTOList.get(i).getProjectId().equals(String.valueOf(project.getId()))) {
//                    caseCountInfoDTOArrayList.add(
//                            CaseCountInfoDTO.builder()
//                                    .count(projectCaseCountDTOList.get(i).getCaseCount())
//                                    .createDateTime(projectCaseCountDTOList.get(i).getCreateDateTime())
//                                    .build());
//                    // 最多只返回最近20次的数据
//                    if (count++ == MongoUtils.DEFAULT_LIMIT_NUM_FOR_PROJECT_CASE_COUNT) {
//                        break;
//                    }
//                }
//            }
//
//            // 由于数据从最近开始拿，此处需要逆序处理（页面上显示的时候 按时间由远到近显示)
//            Collections.reverse(caseCountInfoDTOArrayList);
//
//            res.add(projectCaseCountListDTO);
//        }
//
//        log.info("res:");
//        log.info(res.toString());
//
//        return res;
//    }


    @PostMapping("/batchQuery")
    public List<ProjectCaseCountListDTO> batchQuery() throws IatmException {
        List<ProjectCaseCountListDTO> res = new ArrayList<>();

        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection(MongoUtils.DOC_PROJECT_CASE_COUNT)
                .aggregate(Arrays.asList(
                                new Document("$match",
                                        new Document("isDeleted", false)),
                                new Document("$sort",
                                        new Document("createDateTime", -1L)),
                                new Document("$group",
                                        new Document("_id", "$projectId").
                                                append("datas", new Document("$push", "$$ROOT"))),
                                new Document("$project",
                                        new Document("_id", 0L).
                                                append("datas", new Document("$slice", Arrays.asList("$datas", 20L)))
                                )
                        )
                );

        // aggregateIterable 中 在 db中查询的时候 已经设置了 最多每个项目返回的记录数
        List<Document> documents = Lists.newArrayList(aggregateIterable);
        for (int i = 0; i < documents.size(); i++) {
            ProjectCaseCountListDTO projectCaseCountListDTO = new ProjectCaseCountListDTO();
            List caseCountList  = (List) documents.get(i).get("datas");

            for (int j = 0; j < caseCountList.size(); j++) {
                projectCaseCountListDTO.setProjectId((String) ((Document) caseCountList.get(j)).get("projectId"));
            }

            log.info("caseCountList.size(): {} ", caseCountList.size());
            log.info("projectCaseCountListDTO.getProjectId(): {}", projectCaseCountListDTO.getProjectId());
            Project project = projectService.querySingleProject(projectCaseCountListDTO.getProjectId(), "");
            if (project == null) {
                continue;
            }

            projectCaseCountListDTO.setProjectName(project.getName());
            projectCaseCountListDTO.setGitPath(project.getGitPath());

            ArrayList<CaseCountInfoDTO> caseCountInfoDTOArrayList = new ArrayList<>();
            projectCaseCountListDTO.setCaseCountList(caseCountInfoDTOArrayList);

            for (int j = 0; j < caseCountList.size(); j++) {
                caseCountInfoDTOArrayList.add(
                        CaseCountInfoDTO.builder()
                                .count((Integer) ((Document) caseCountList.get(j)).get("caseCount"))
                                .createDateTime((Date) ((Document) caseCountList.get(j)).get("createDateTime"))
                                .build());
            }

            // 由于数据从最近开始拿，此处需要逆序处理（页面上显示的时候 按时间由远到近显示)
            Collections.reverse(caseCountInfoDTOArrayList);

            res.add(projectCaseCountListDTO);
        }

        return res;
    }

    @GetMapping("/getProjectCaseCount")
    public void getProjectCaseCount() {
        List<Project> projectList = mongoTemplate.find(new Query(), Project.class);
        for (Project project : projectList) {
            log.info("================= start to fetch caseCount for project: " + project.getName());
            try {
                workflowClient.runCase("auto_get_project_case_count_run_by_test_engine", project);
            } catch (JsonProcessingException e) {
                log.error("", e);
                //todo
            }
        }

    }
}
