package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.utils.RedisKeyUtil;
import com.nowcoder.community.utils.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.TemplateEngine;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    // 1.帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 2.帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;


    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        int offset = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);

                        // 二级缓存
                        String redisKey = RedisKeyUtil.getPostListCacheKey(offset, limit);
                        List<DiscussPost> postList = (List<DiscussPost>) redisTemplate.opsForValue().get(redisKey);
                        if (postList != null) { // 说明二级缓存存在，那么直接返回即可
                            logger.debug("load post list from redis.");
                            return postList;
                        }

                        // 否则查询数据库
                        logger.debug("load post list from DB.");
                        postList = discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                        // 将查询到的帖子列表缓存到二级缓存中
                        redisTemplate.opsForValue().set(redisKey, postList);

                        return postList;
                    }
                });

        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        // 先查看二级缓存，有则返回
                        String redisKey = RedisKeyUtil.getPostRowsCacheKey();

                        Integer rows = (Integer) redisTemplate.opsForValue().get(redisKey);
                        if (rows != null) {
                            logger.debug("load post rows from redis.");
                            return rows;
                        }

                        // 否则查询数据库
                        logger.debug("load post Rows from DB.");
                        rows = discussPostMapper.selectDiscussPostRows(key);
                        // 将行数缓存到二级缓存中
                        redisTemplate.opsForValue().set(redisKey, rows);

                        return rows;
                    }
                });

    }

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) { // 如果是访问首页并且是访问热帖才访问缓存
            return postListCache.get(offset + ":" + limit);
        }

        logger.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public Integer findDiscussPostRows(int userId) {
        if (userId == 0) { // 首页查询时才访问缓存
            return postRowsCache.get(userId);
        }

        logger.debug("load post rows from DB.");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}
