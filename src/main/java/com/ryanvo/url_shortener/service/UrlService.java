package com.ryanvo.url_shortener.service;

import com.ryanvo.url_shortener.model.UrlMapping;
import com.ryanvo.url_shortener.repository.UrlRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UrlService {

    private final UrlRepository repository;
    private final SnowflakeIdGenerator idGenerator;
    private final StringRedisTemplate redisTemplate;

    public UrlService(UrlRepository repository, StringRedisTemplate redisTemplate) {
        this.repository = repository;
        this.idGenerator = new SnowflakeIdGenerator(1);
        this.redisTemplate = redisTemplate;
    }

    public String shortenUrl(String originalUrl, String customAlias) {
        String shortCode;
        if (customAlias != null) {
            if (repository.findByShortCode(customAlias).isPresent()) {
                throw new IllegalArgumentException("Alias already taken");
            }
            shortCode = customAlias;
        }
        else {
            long id = idGenerator.nextId();
            shortCode = Base62Converter.encode(id);
        }

        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl(originalUrl);
        mapping.setShortCode(shortCode);

        repository.save(mapping);
        redisTemplate.opsForValue().set(shortCode, originalUrl, 24, TimeUnit.HOURS);

        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        // Check Redis (RAM)
        String cachedUrl = redisTemplate.opsForValue().get(shortCode);
        if (cachedUrl != null) {
            System.out.println("CACHE HIT: " + shortCode);
            return cachedUrl;
        }

        // Check Database (Disk)
        System.out.println("CACHE MISS: " + shortCode);
        long id = Base62Converter.decode(shortCode);
        String dbUrl = repository.findById(id)
                .map(UrlMapping::getLongUrl)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        // Save found URL to Redis
        redisTemplate.opsForValue().set(shortCode, dbUrl, 24, TimeUnit.HOURS);

        return dbUrl;
    }
}