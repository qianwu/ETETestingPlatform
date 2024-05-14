package org.charlotte.e2ecore.endpoint;

import org.charlotte.e2ecore.service.CollectionService;
import org.charlotte.e2edomain.Category;
import org.charlotte.e2edomain.dto.CategoryBatchQueryDTO;
import org.charlotte.e2edomain.dto.CategoryCreateDTO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Resource
    private CollectionService collectionService;

    @Resource
    private MongoTemplate mongoTemplate;

    @PostMapping("/create")
    public Category create(@RequestBody CategoryCreateDTO dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .projectId(dto.getProjectId())
                .status("0")
                .build();

        return collectionService.saveOrUpdate(category);
    }

    @PostMapping("batch")
    public List<Category> batchQuery(@RequestBody CategoryBatchQueryDTO dto) {
        return mongoTemplate.find(new Query(), Category.class);
    }
}
