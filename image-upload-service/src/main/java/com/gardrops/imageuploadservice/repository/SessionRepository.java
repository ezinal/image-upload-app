package com.gardrops.imageuploadservice.repository;

import com.gardrops.imageuploadservice.model.Session;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@Data
public class SessionRepository {

    private final Duration sessionTtl;
    private final RedisTemplate<String, Session> redisTemplate;

    public SessionRepository(RedisTemplate<String, Session> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.sessionTtl = Duration.ofHours(1);
    }

    public void saveSession(Session session) {
        String key = "session:" + session.getSessionId();
        redisTemplate.opsForValue().set(key, session, sessionTtl);
    }

}
