package org.charlotte.e2ecore.service;

import org.charlotte.e2ecore.utils.UUIDUtil;
import org.charlotte.e2edomain.LockDocument;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 基于MongoTemplate，提供分布式锁能力
 */
@Slf4j
@Service
public class LockService {

    private static final long DEFAULT_EXPIRATION = 1000 * 60;

    @Resource
    private MongoTemplate mongoTemplate;

    public String acquire(String key) {
        return this.acquire(key, DEFAULT_EXPIRATION);
    }

    /**
     * 获取key，并设置锁过期毫秒数
     *
     * @return token
     */
    public String acquire(String key, long expiration) {
        Query query = Query.query(Criteria.where("_id").is(key));
        String token = UUIDUtil.getUUID32();

        Update update = new Update()
                .setOnInsert("_id", key)
                .setOnInsert("expireAt", System.currentTimeMillis() + expiration)
                .setOnInsert("token", token);

        LockDocument doc;
        FindAndModifyOptions options = new FindAndModifyOptions().upsert(true)
                .returnNew(true);
        try {
            //尝试获取锁
            doc = mongoTemplate.findAndModify(query, update, options, LockDocument.class);
        } catch (Exception e) {
            log.error("Fail to acquire {}!", key);
            return null;
        }
        boolean locked = (doc != null) && token.equals(doc.getToken());

        // 如果已过期
        if (!locked && doc.getExpireAt() < System.currentTimeMillis()) {
            DeleteResult deleted = this.mongoTemplate.remove(
                    Query.query(Criteria.where("_id").is(key)
                            .and("token").is(doc.getToken())
                            .and("expireAt").is(doc.getExpireAt())),
                    LockDocument.class);
            if (deleted.getDeletedCount() >= 1) {
                // 成功释放锁， 再次尝试获取锁
                return this.acquire(key, expiration);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Tried to acquire lock for key {} with token {} . Locked: {}",
                    key, token, locked);
        }
        return locked ? token : null;
    }


    public boolean release(String key, String token) {
        Query query = Query.query(Criteria.where("_id").is(key)
                .and("token").is(token));
        DeleteResult deleted = mongoTemplate.remove(query, LockDocument.class);
        boolean released = deleted.getDeletedCount() == 1;
        if (released) {
            log.debug("Remove query successfully affected 1 record for key {} with token {}",
                    key, token);
        } else if (deleted.getDeletedCount() > 0) {
            log.error("Unexpected result from release for key {} with token {}, released {}",
                    key, token, deleted);
        } else {
            log.error("Remove query did not affect any records for key {} with token {}",
                    key, token);
        }

        return released;
    }

    public boolean refresh(String key, String token,
                           long expiration) {
        Query query = Query.query(Criteria.where("_id").is(key)
                .and("token").is(token));
        Update update = Update.update("expireAt",
                System.currentTimeMillis() + expiration);
        UpdateResult updated =
                mongoTemplate.updateFirst(query, update, LockDocument.class);

        final boolean refreshed = updated.getModifiedCount() == 1;
        if (refreshed) {
            log.debug("Refresh query successfully affected 1 record for key {} " +
                    "with token {}", key, token);
        } else if (updated.getModifiedCount() > 0) {
            log.error("Unexpected result from refresh for key {} with token {}, " +
                    "released {}", key, token, updated);
        } else {
            log.warn("Refresh query did not affect any records for key {} with token {}. " +
                            "This is possible when refresh interval fires for the final time " +
                            "after the lock has been released",
                    key, token);
        }

        return refreshed;
    }


}
