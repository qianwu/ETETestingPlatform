package org.charlotte.e2ecore.service;

import org.charlotte.e2ecore.remote.github.action.WorkflowClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.charlotte.e2edomain.Case;
import org.charlotte.e2edomain.ChainData;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class ExecuteService {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private WorkflowClient workflowClient;

    Configuration jsonpathConfiguration;

    public ExecuteService() {
        JsonProvider jsonProvider = new JacksonJsonProvider();
        MappingProvider mappingProvider = new JacksonMappingProvider();

        jsonpathConfiguration = Configuration.defaultConfiguration()
                .jsonProvider(jsonProvider)
                .mappingProvider(mappingProvider)
                .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
    }

    public void doExecute(ChainData chainData) {
        if (!chainData.hasNextCase()) {
            log.info("has no more case to be executed");
            return;
        }

        Case nextCase = chainData.nextCase();
        Map<String, String> params = params(chainData);
        try {
            workflowClient.dispatch(chainData.getId().toString(), nextCase, params, chainData.getLatestFinishedCaseIndex(), chainData.getEnv());
        } catch (JsonProcessingException e) {
            log.error("", e);
            //todo
        }
    }

    Map<String, String> params(ChainData executionContext) {
        return executionContext.getGlobalData();
    }
}
