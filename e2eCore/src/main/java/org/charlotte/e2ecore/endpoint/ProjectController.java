package org.charlotte.e2ecore.endpoint;

import org.charlotte.e2ecore.exception.BizError;
import org.charlotte.e2ecore.exception.IatmException;
import org.charlotte.e2ecore.remote.github.action.WorkflowClient;
import org.charlotte.e2ecore.service.CollectionService;
import org.charlotte.e2ecore.service.ProjectService;
import org.charlotte.e2ecore.utils.MongoUtils;
import org.charlotte.e2ecore.utils.TokenUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.charlotte.e2edomain.Project;
import org.charlotte.e2edomain.dto.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    public ProjectController() {
        this.objectMapper = new ObjectMapper();
    }

    @Resource
    private WorkflowClient workflowClient;

    @Resource
    RestTemplate restTemplate;

    ObjectMapper objectMapper;

    @Value("${github.token}")
    String authorization_token;

    @Value("${github.accept.default}")
    String github_accept_header;


    @Resource
    private CollectionService collectionService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ProjectService projectService;

    private Configuration jsonpathConfiguration;

    @PostConstruct
    public void init() {
        jsonpathConfiguration = Configuration.defaultConfiguration()
                .jsonProvider(new JacksonJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS);
    }

    @PostMapping("/create")
    public Project create(@RequestBody ProjectCreateDTO dto) {
        Map<String, Object> userClaim = TokenUtils.getUserClaim();
        String email = (String) userClaim.get("email");
        String name = (String) userClaim.get("name");

        Project project = Project.builder()
                .name(dto.getName())
                .gitOwner(dto.getGitOwner())
                .creator(email)
                .gitPath(dto.getGitPath())
                .workflowId(dto.getWorkflowId())
                .description(dto.getDescription())
                .isDeleted(Boolean.FALSE)
                .createDateTime(LocalDateTime.now())
                .build();
        return collectionService.saveOrUpdate(project);
    }

    @PostMapping("/update")
    public Project update(@RequestBody ProjectUpdateDTO dto) throws Exception {
        Map<String, Object> userClaim = TokenUtils.getUserClaim();
        String email = (String) userClaim.get("email");
        String name = (String) userClaim.get("name");

        Query query = new Query(Criteria.where("_id").is(new ObjectId(dto.getId())));
        Project project = mongoTemplate.findOne(query, Project.class, MongoUtils.DOC_PROJECT);
        if (Objects.isNull(project)) {
            throw new Exception("[UPDATE FAIL!] case '" + dto.getId() + "' doest not exists!");
        }
        project.setName(dto.getName());
        project.setGitPath(dto.getGitPath());
        project.setWorkflowId(dto.getWorkflowId());
        project.setDescription(dto.getDescription());
        project.setUpdateDateTime(LocalDateTime.now());

        return collectionService.saveOrUpdate(project);
    }

    @PostMapping("/updateProjectCaseInfo")
    public Project updateProjectCaseInfo(@RequestBody ProjectUpdateDTO dto) throws Exception {
        log.info("start updateProjectCaseInfo with dto:");
        log.info(dto.toString());

        Query query = new Query(Criteria.where("_id").is(new ObjectId(dto.getId())));
        Project project = mongoTemplate.findOne(query, Project.class, MongoUtils.DOC_PROJECT);
        if (Objects.isNull(project)) {
            throw new Exception("[UPDATE FAIL!] case '" + dto.getId() + "' doest not exists!");
        }

        // 如下的字段 考虑到安全，把更新如下字段的逻辑 单独封装到一个方法（没有集成到 update方法中），并且该方法只允许更新如下字段（不能更新projectName，workflowID等字段)
        project.setCaseCount(dto.getCaseCount());
        project.setTagList(dto.getTagList());
        project.setPackageList(dto.getPackageList());
        project.setClassNameList(dto.getClassNameList());
        project.setUpdateDateTime(LocalDateTime.now());

        return collectionService.saveOrUpdate(project);
    }



    @GetMapping("/query")
    public ProjectDTO getProject(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "id", required = false) String id) throws IatmException {
        Project project = projectService.querySingleProject(id, name);
        System.out.println("project");
        System.out.println(project);
//        ProjectDTO projectDTO = EntityUtils.copyData(project, ProjectDTO.class);
        return ProjectDTO.builder()
                .id(project.getId().toHexString())
                .name(project.getName())
                .gitPath(project.getGitPath())
                .workflowId(project.getWorkflowId())
                .gitOwner(project.getGitOwner())
                .description(project.getDescription())
                .creator(project.getCreator())
                .build();
    }

    @GetMapping("/checkProjectExistsByName")
    public String checkProjectExistsByProjectName(@RequestParam(value = "name") String name) {
        String res = "exists";
        try {
            Project project = projectService.querySingleProjectByName(name);
            System.out.println(project);
        } catch (IatmException e) {
            System.out.println(e);
            if (BizError.NO_SUCH_DATA.getCode().equals(e.getCode())) {
                res = "notExists";
            }
        }

        return res;
    }

    @GetMapping("/checkProjectExistsByGitPath")
    public String checkProjectExistsByGitPath(@RequestParam(value = "gitPath") String gitPath) {
        String res = "exists";
        try {
            Project project = projectService.querySingleProjectByGitPath(gitPath);
            System.out.println(project);
        } catch (IatmException e) {
            System.out.println(e);
            if (BizError.NO_SUCH_DATA.getCode().equals(e.getCode())) {
                res = "notExists";
            }
        }

        return res;
    }

    @DeleteMapping("delete")
    public void delete(@RequestParam("id") String id) throws Exception {
        Project project = collectionService.getDocumentById(id, Project.class);
        if (project.getIsDeleted()) {
            return;
        }
        projectService.deleteProject(id);
    }

    @PostMapping("batchQuery")
    public List<Project> batchQuery(@RequestBody ProjectBatchQueryDTO dto) {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("createDateTime")));

        return mongoTemplate.find(
                query.addCriteria(Criteria.where("isDeleted").is(false)).limit(Objects.isNull(dto.getLimit()) ? MongoUtils.DEFAULT_LIMIT_NUM : dto.getLimit()),
                Project.class);
    }

    @GetMapping("list")
    public List<Project> list() {
        return mongoTemplate.find(new Query(), Project.class);
    }


    @GetMapping("workflows")
    public List<GitActionWorkflowDTO> getWorkflows(@RequestParam("owner") String owner,
                                                   @RequestParam("repo") String repo) {
        String path = "https://gitdomain/api/v3/repos/" + owner + "/" + repo + "/actions/workflows";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, github_accept_header);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, authorization_token);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> exchange = restTemplate.exchange(path, HttpMethod.GET, httpEntity, String.class);
        log.info(exchange.getBody());
        DocumentContext context = JsonPath.using(jsonpathConfiguration).parse(exchange.getBody());
        Integer workflowCount = context.read("$.workflows.length()", Integer.class);
        List<GitActionWorkflowDTO> result = new ArrayList<>(workflowCount);
        for (int i = 0; i < workflowCount; i++) {
            String name = context.read("$.workflows[" + i + "].name", String.class);
            Long id = context.read("$.workflows[" + i + "].id", Long.class);
            result.add(GitActionWorkflowDTO.builder().workflowId(id).workflowName(name).build());
        }
        return result;
    }


    @GetMapping("/getProjectCaseCount")
    public void getProjectCaseCount() {
        List<Project> projectList = mongoTemplate.find(new Query(), Project.class);
        for (Project project : projectList) {
            log.info("auto fetch case count of project: " + project.getName());
            try {
                workflowClient.runCase("auto_get_project_case_count_run_by_test_engine", project);
            } catch (JsonProcessingException e) {
                log.error("runCase Error ", e);
                //todo
            }
        }
    }


}
