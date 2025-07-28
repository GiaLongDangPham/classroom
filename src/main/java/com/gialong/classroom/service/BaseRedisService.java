package com.gialong.classroom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class BaseRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;

    public BaseRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setTimeToLive(String key, long duration, TimeUnit timeUnit) {
        redisTemplate.expire(key, duration, timeUnit);
    }

    public void hashSet(String key, String field, Object value) {
        hashOperations.put(key, field, value);
    }

    public boolean hashExists(String key, String field) {
        return hashOperations.hasKey(key, field);
    }

    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public Map<String, Object> getField(String key){
        return hashOperations.entries(key);
    }

    public Object hashGet(String key, String field){
        return hashOperations.get(key, field);
    }

    public List<Object> hashGetByFieldPrefix(String key, String fieldPrefix){
        List<Object> objects = new ArrayList<>();
        Map<String, Object> hashEntries = hashOperations.entries(key);
        for (Map.Entry<String, Object> entry : hashEntries.entrySet()) {
            if(entry.getKey().startsWith(fieldPrefix)){
                objects.add(entry.getValue());
            }
        }
        return objects;
    }

    public Set<String> getFieldPrefixes(String key){
        return hashOperations.entries(key).keySet();
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }

    public void delete(String key, String field){
        hashOperations.delete(key, field);
    }

    public void delete(String key, List<String> fields){
        for(String field : fields){
            hashOperations.delete(key, field);
        }
    }
}
