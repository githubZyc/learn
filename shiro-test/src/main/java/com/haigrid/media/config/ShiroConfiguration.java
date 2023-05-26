package com.haigrid.media.config;

import com.haigrid.media.security.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/5/24 16:38
 */
@Configuration
public class ShiroConfiguration {
    /**
     *定义shiroFilter工厂，设置对应的过滤条件和跳转条件
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 过滤器链定义映射
        Map<String, String> map = new LinkedHashMap<>();

        /**
         * anon:所有url都可以匿名访问
         * authc:所有url都必须认证通过才可以访问
         *  过滤链定义，从上向下顺序执行，authc 应放在 anon 下面
         */
        //登录接口可以匿名访问
        map.put("/sys-user/login","anon");
        //所有接口必须认证后才可访问
        map.put("/**","authc");

        // 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了, 位置放在 anon、authc下面
        map.put("logout","logout");

        //未认证跳转次接口
        shiroFilterFactoryBean.setLoginUrl("/user/unAuth");
        //未授权跳转次接口
        shiroFilterFactoryBean.setUnauthorizedUrl("/user/unAuthorized");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);

        return shiroFilterFactoryBean;
    }


    /**
     * 由于我们的密码校验逻辑是交给Shiro的SimpleAuthenticationInfo进行处理，所以这里需要配置凭证匹配器
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // 散列算法:这里使用SHA-1算法;
        hashedCredentialsMatcher.setHashAlgorithmName("SHA-1");
        // 散列的次数
        hashedCredentialsMatcher.setHashIterations(1024);
        return hashedCredentialsMatcher;
    }


    /**
     * 将自定义的realm校验方式注入到spring容器中
     * @return
     */
    @Bean
    public UserRealm myRealm() {
        UserRealm myRealm = new UserRealm();
        myRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myRealm;
    }


    /**
     * 权限管理，配置主要是Realm的管理认证
     * @return
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myRealm());
//        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }


    /**
     * 自定义sessionManager
     * @return
     */
//    @Bean
//    public SessionManager sessionManager() {
//        MySessionManager mySessionManager = new MySessionManager();
//        mySessionManager.setSessionDAO(new EnterpriseCacheSessionDAO());
//        return mySessionManager;
//    }



    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
