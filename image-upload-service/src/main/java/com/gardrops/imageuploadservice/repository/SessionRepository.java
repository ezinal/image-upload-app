package com.gardrops.imageuploadservice.repository;

import com.gardrops.imageuploadservice.model.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
public class SessionRepository {
    private static final String SESSION_KEY_PREFIX = "session:";

    private final Duration sessionTtl;
    private final RedisTemplate<String, Session> redisTemplate;

    public SessionRepository(RedisTemplate<String, Session> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.sessionTtl = Duration.ofHours(1);
    }

    public void saveSession(Session session) {
        String key = SESSION_KEY_PREFIX + session.getSessionId();
        redisTemplate.opsForValue().set(key, session, sessionTtl);
    }

    public Session findSession(UUID sessionId) {
        String key = SESSION_KEY_PREFIX + sessionId;
        return redisTemplate.opsForValue().get(key);
    }

    public boolean exists(UUID sessionId) {
        String key = SESSION_KEY_PREFIX + sessionId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
