package com.fit.config.shiro.core;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @AUTO 验证器，增加了登录次数校验功能
 * @Author AIM
 * @DATE 2019/3/22
 */
public class RetryLimitCredentialsMatcher extends HashedCredentialsMatcher {

    private static final Logger logger = LoggerFactory.getLogger(RetryLimitCredentialsMatcher.class);

    private int maxRetryNum = 5;
    private EhCacheManager shiroEhcacheManager;

    public RetryLimitCredentialsMatcher(EhCacheManager shiroEhcacheManager) {
        setHashAlgorithmName("md5");// 加密方式
        setHashIterations(1);// 循环加密次数
        this.shiroEhcacheManager = shiroEhcacheManager;
    }

    /**
     * @param shiroEhcacheManager EhCache缓存对象
     * @param hashAlgorithmName   加密方式
     * @param hashIterations      循环加密次数
     */
    public RetryLimitCredentialsMatcher(EhCacheManager shiroEhcacheManager, String hashAlgorithmName, int hashIterations) {
        this.shiroEhcacheManager = shiroEhcacheManager;
        setHashAlgorithmName(hashAlgorithmName);// 加密方式
        setHashIterations(hashIterations);// 循环加密次数
    }

    public void setMaxRetryNum(int maxRetryNum) {
        this.maxRetryNum = maxRetryNum;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        logger.info("##################执行Shiro登录次数##################");
        Cache<String, AtomicInteger> passwordRetryCache = shiroEhcacheManager.getCache("loginRecordCache");
        String username = (String) token.getPrincipal();
        //retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username);
        if (null == retryCount) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        if (retryCount.incrementAndGet() > maxRetryNum) {
            logger.info("用户[{}]进行登录验证..失败验证超过{}次", username, maxRetryNum);
            throw new ExcessiveAttemptsException("用户名登陆错误次数超过次数");
        }
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            //clear retry data
            passwordRetryCache.remove(username);
        } else {
            logger.info("用户[{}]进行登录验证..失败验证超过{}次", username, retryCount);
            throw new ExcessiveAttemptsException("用户名或密码错误！错误次数：" + retryCount);
        }
        return matches;
    }
}
