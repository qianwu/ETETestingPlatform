package org.charlotte.e2ecore.service;

import org.charlotte.e2ecore.exception.BizError;
import org.charlotte.e2ecore.exception.IatmException;
import org.charlotte.e2ecore.utils.MongoUtils;
import org.charlotte.e2edomain.Project;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：charlotte
 * @date ：Created in 17/12/21 3:13 PM
 */
@Slf4j
@Service
public class ProjectService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private CollectionService collectionService;

    public List<Project> getProjectListByIdList(List<String> projectIdList) {
        Query query = new Query(Criteria.where("_id").in(projectIdList));
        return mongoTemplate.find(query, Project.class, MongoUtils.DOC_PROJECT);
    }


    public void deleteProject(String projectId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(projectId)));
        Update update = new Update();
        update.set("isDeleted", Boolean.TRUE);
        mongoTemplate.updateMulti(query, update, Project.class, MongoUtils.DOC_PROJECT);
    }


    /**
     * return one certain case info according to inputs
     *
     * @param id     ObjectId in mongoDB
     * @param projectName project's name
     * @return org.charlotte.e2edomain.Project
     */
    public Project querySingleProject(String id, String projectName) throws IatmException {
        //直接根据主键查询
        if (!StringUtils.isEmpty(id)) {
            return collectionService.getDocumentById(id, Project.class);
        }

        Query query = new Query(Criteria.where("name").is(projectName));
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<Project> projects = mongoTemplate.find(query, Project.class, MongoUtils.DOC_PROJECT);

        if (CollectionUtils.isEmpty(projects)) {
            throw new IatmException(BizError.NO_SUCH_DATA);
        }
        if (projects.size() > 1) {
            throw new IatmException(BizError.MORE_THAN_ONE_RECORD_FOUND);
        }
        return projects.get(0);
    }

    /**
     * return one certain project info according to inputs
     *
     * @param projectName project's name
     * @return org.charlotte.e2edomain.Project
     */
    public Project querySingleProjectByName(String projectName) throws IatmException {
        Query query = new Query(Criteria.where("name").is(projectName));
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<Project> projects = mongoTemplate.find(query, Project.class, MongoUtils.DOC_PROJECT);

        if (CollectionUtils.isEmpty(projects)) {
            throw new IatmException(BizError.NO_SUCH_DATA);
        }
        if (projects.size() > 1) {
            throw new IatmException(BizError.MORE_THAN_ONE_RECORD_FOUND);
        }
        return projects.get(0);
    }

    /**
     * return one certain project info according to inputs
     *
     * @param gitPath project's gitPath String type
     * @return org.charlotte.e2edomain.Project
     */
    public Project querySingleProjectByGitPath(String gitPath) throws IatmException {
        Query query = new Query(Criteria.where("gitPath").is(gitPath));
        query.addCriteria(Criteria.where("isDeleted").is(false));
        List<Project> projects = mongoTemplate.find(query, Project.class, MongoUtils.DOC_PROJECT);

        if (CollectionUtils.isEmpty(projects)) {
            throw new IatmException(BizError.NO_SUCH_DATA);
        }
        if (projects.size() > 1) {
            throw new IatmException(BizError.MORE_THAN_ONE_RECORD_FOUND);
        }
        return projects.get(0);
    }

    public Long countAll() {
        return mongoTemplate.count(new Query(), MongoUtils.DOC_PROJECT);
    }
}
