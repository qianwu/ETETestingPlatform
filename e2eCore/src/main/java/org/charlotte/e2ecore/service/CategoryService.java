package org.charlotte.e2ecore.service;

import org.charlotte.e2ecore.utils.MongoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class CategoryService {
    @Resource
    private MongoTemplate mongoTemplate;

    public Long countAll() {
        return mongoTemplate.count(new Query(), MongoUtils.DOC_CATEGORY);
    }
}
