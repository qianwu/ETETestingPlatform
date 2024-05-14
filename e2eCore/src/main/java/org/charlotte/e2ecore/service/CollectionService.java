package org.charlotte.e2ecore.service;

import org.charlotte.e2ecore.exception.BizError;
import org.charlotte.e2ecore.exception.IatmException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
public class CollectionService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Transactional(rollbackFor = Throwable.class)
    public <T> T saveOrUpdate(T t) {
        return mongoTemplate.save(t);
    }

    public <T> T getDocumentById(String id, Class<T> clazz) throws IatmException {
        T t = null;
        try{
            t = mongoTemplate.findById(new ObjectId(id), clazz);
        } catch (Exception e){
            log.error("getDocumentById fail", e);
            throw new IatmException(BizError.NO_SUCH_DATA);
        }

        return t;
    }

//    public void clearAllCollection() {
//        mongoTemplate.dropCollection(Project.class);
//        mongoTemplate.dropCollection(Category.class);
//        mongoTemplate.dropCollection(Case.class);
//        mongoTemplate.dropCollection(CaseData.class);
//        mongoTemplate.dropCollection(Chain.class);
//        mongoTemplate.dropCollection(ChainData.class);
//    }
}
