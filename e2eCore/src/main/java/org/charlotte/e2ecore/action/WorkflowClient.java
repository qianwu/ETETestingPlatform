package org.charlotte.e2ecore.action;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.charlotte.e2edomain.Case;
import org.charlotte.e2edomain.Project;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class WorkflowClient {

    public WorkflowClient() {
        this.objectMapper = new ObjectMapper();
    }

    @Resource
    RestTemplate restTemplate;

    ObjectMapper objectMapper;

    @Value("${github.token}")
    String authorization_token;

    @Value("${github.accept.default}")
    String github_accept_header;


    public void dispatch(String executionId, Case c, Object params, Integer caseIndex, String env) throws JsonProcessingException {
        String paramsStr = objectMapper.writeValueAsString(objectMapper.writeValueAsString(params));
        String path = targetURL(c);
        WorkflowDispatchRequestBody requestBody = new WorkflowDispatchRequestBody("master");
        requestBody.addInput(WorkflowDispatchRequestBody.CALLBACK_API_KEY, "http://callback");
        requestBody.addInput(WorkflowDispatchRequestBody.CASE_ID_KEY, c.getCaseNo());
        requestBody.addInput(WorkflowDispatchRequestBody.CASE_INDEX_KEY, String.valueOf(caseIndex.intValue() + 1));
        requestBody.addInput(WorkflowDispatchRequestBody.ENV_KEY, env);
        requestBody.addInput(WorkflowDispatchRequestBody.EXECUTION_ID_KEY, executionId);
        requestBody.addInput(WorkflowDispatchRequestBody.PARAMS_KEY, paramsStr);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, github_accept_header);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, authorization_token);
        HttpEntity<WorkflowDispatchRequestBody> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(path, httpEntity, String.class);
        log.info("dispatching... httpEntity={}, responseEntity={}", httpEntity, responseEntity);
    }


    public void runCase(String caseNo, Project project, String executionId, String env, Object params) throws JsonProcessingException {
        // todo 此方法用于执行通过 test-engine 执行 regression/case

        String paramsStr = objectMapper.writeValueAsString(objectMapper.writeValueAsString(params));

        StringBuilder stringBuilder = new StringBuilder(project.getGitPath());
        stringBuilder.append("/actions").append("/workflows").append("/").append(String.valueOf(project.getWorkflowId())).append("/dispatches");
        String path = stringBuilder.toString().replace("path", "repopath");
        WorkflowDispatchRequestBody requestBody = new WorkflowDispatchRequestBody("master");
        requestBody.addInput(WorkflowDispatchRequestBody.CALLBACK_API_KEY, "");
        requestBody.addInput(WorkflowDispatchRequestBody.CASE_ID_KEY, caseNo);
        requestBody.addInput(WorkflowDispatchRequestBody.CASE_INDEX_KEY, "0");
//        requestBody.addInput(WorkflowDispatchRequestBody.ENV_KEY, "dev");

        requestBody.addInput(WorkflowDispatchRequestBody.ENV_KEY, env);
        requestBody.addInput(WorkflowDispatchRequestBody.PARAMS_KEY, paramsStr);

        // todo: 此处需要设计一下 run case的时候 此 executionId 的业务语义（run regression时必须 设置 executionId？ 需要增加caseData集合，executionId在 CaseData中）
        // lintest 需要判断 caseNo查找到多少个case？ 如果是1个 则 返回 case log， 如果是多个 则返回 xxx.report.html ？
        // 或者 runCase的时候 lintest增加判断：如果有 传入有效的24位 executionId & projectID & caseNo & env， 则把caselog 统一保存到 test-engine中？再新建一个集合？
        executionId = executionId.trim();
        System.out.println(executionId);
        System.out.println(executionId.length());
        if (executionId.length() == 24) {
            // 如果只是 fetch project caseCount or get Case Structure, 则调用 runCase方法时无需传入 executionId
            requestBody.addInput(WorkflowDispatchRequestBody.EXECUTION_ID_KEY, executionId);
        } else {
            log.warn("Invalid executionId format: " + executionId);
        }
        // projectId
        requestBody.addInput(WorkflowDispatchRequestBody.PROJECT_ID_KEY, String.valueOf(project.getId()));

        requestBody.addInput(WorkflowDispatchRequestBody.PARAMS_KEY, "{}");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, github_accept_header);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, authorization_token);
        HttpEntity<WorkflowDispatchRequestBody> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(path, httpEntity, String.class);
        log.info("runCase start... httpEntity={}, responseEntity={}", httpEntity, responseEntity);
    }

    //todo magic number path.
    public void runCase(String caseNo, Project project) throws JsonProcessingException {
        StringBuilder stringBuilder = new StringBuilder(project.getGitPath());
        stringBuilder.append("/actions").append("/workflows").append("/").append(String.valueOf(project.getWorkflowId())).append("/dispatches");
        String path = stringBuilder.toString().replace("gitbuilddomain", "gitbuilddomain/api/v3/repos");
        WorkflowDispatchRequestBody requestBody = new WorkflowDispatchRequestBody("master");
        requestBody.addInput(WorkflowDispatchRequestBody.CALLBACK_API_KEY, "");
        requestBody.addInput(WorkflowDispatchRequestBody.CASE_ID_KEY, caseNo);
        requestBody.addInput(WorkflowDispatchRequestBody.CASE_INDEX_KEY, "0");
        requestBody.addInput(WorkflowDispatchRequestBody.ENV_KEY, "dev");

        requestBody.addInput(WorkflowDispatchRequestBody.EXECUTION_ID_KEY, "");

        // projectId
        requestBody.addInput(WorkflowDispatchRequestBody.PROJECT_ID_KEY, String.valueOf(project.getId()));

        requestBody.addInput(WorkflowDispatchRequestBody.PARAMS_KEY, "{}");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, github_accept_header);
        httpHeaders.add(HttpHeaders.AUTHORIZATION, authorization_token);
        HttpEntity<WorkflowDispatchRequestBody> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(path, httpEntity, String.class);
        log.info("runCase start... httpEntity={}, responseEntity={}", httpEntity, responseEntity);
    }


    public String targetURL(Case c) {
        StringBuilder stringBuilder = new StringBuilder(c.getPath());
        stringBuilder.append("/actions").append("/workflows").append("/").append(c.getWorkflowId()).append("/dispatches");
        return stringBuilder.toString().replace("path", "path/api/v3/repos");
    }



    public String callbackURI(String executionId) {
        return "";
    }


    @Getter
    @RequiredArgsConstructor
    public static class WorkflowDispatchRequestBody {

        public static final String EXECUTION_ID_KEY = "execution_id";

        public static final String PROJECT_ID_KEY = "project_id";

        public static final String CASE_ID_KEY = "case_id";

        public static final String ENV_KEY = "env";

        public static final String CASE_INDEX_KEY = "case_index";

        public static final String PARAMS_KEY = "params";

        public static final String CALLBACK_API_KEY = "callback_api";

        private final String ref;

        private Map<String, Object> inputs = new HashMap<>(16);

        public void addInput(String inputKey, Object inputValue) {
            inputs.put(inputKey, inputValue);
        }
    }
}
