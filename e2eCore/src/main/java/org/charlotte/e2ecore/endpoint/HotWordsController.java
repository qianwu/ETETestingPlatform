package org.charlotte.e2ecore.endpoint;


import org.charlotte.e2ecore.service.CollectionService;
import org.charlotte.e2ecore.utils.FetchHotword;
import org.charlotte.e2ecore.utils.MongoUtils;
import org.charlotte.e2edomain.HotWords;
import org.charlotte.e2edomain.dto.HotWordsBatchQueryDTO;
import org.charlotte.e2edomain.dto.MetaBaseLoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/hotwords")
public class HotWordsController {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private CollectionService collectionService;


    @PostMapping("/batchQuery")
    public List<HotWords> batchQuery(@RequestBody HotWordsBatchQueryDTO dto) {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("createDateTime")));

        return mongoTemplate.find(
                query.addCriteria(Criteria.where("isDeleted").is(false)).limit(Objects.isNull(dto.getLimit()) ? MongoUtils.DEFAULT_LIMIT_NUM : dto.getLimit()),
                HotWords.class);
    }

    @GetMapping("/list")
    public List<HotWords> list() {
        return mongoTemplate.find(new Query(), HotWords.class);
    }


    @PostMapping("/fetchLatestHotword")
    public Boolean fetchLatestHotword(@RequestBody MetaBaseLoginDTO dto) {
        try {
            String res = null;

            res = FetchHotword.fetchHotword(dto.getUsername(), dto.getPassword());

            HotWords hotWords = HotWords.builder()
                    .keyWordAndSearchNum(res)
                    .isDeleted(Boolean.FALSE)
                    .createDateTime(LocalDateTime.now())
                    .build();
            collectionService.saveOrUpdate(hotWords);

        } catch (Exception e) {
            System.out.println(e);
            log.error("fetch hotword error!: {} ", e);
            return false;
        }


        // todo 获取 各个 keyword的近10天的搜索热度趋势数据
//        try {
//            res = FetchHotword.fetchHistorySearchCountByHotword(dto.getUsername(), dto.getPassword());
//        } catch (Exception e) {
//            log.error("fetch fetchHotword error!: " + e.getStackTrace());
//        }


        return true;
    }


}
