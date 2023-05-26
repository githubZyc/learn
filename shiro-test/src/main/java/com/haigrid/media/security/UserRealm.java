package com.haigrid.media.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haigrid.media.entity.SysMenu;
import com.haigrid.media.entity.SysRole;
import com.haigrid.media.entity.SysUser;
import com.haigrid.media.entity.SysUserRole;
import com.haigrid.media.service.SysMenuService;
import com.haigrid.media.service.SysRoleService;
import com.haigrid.media.service.SysUserRoleService;
import com.haigrid.media.service.SysUserService;
import com.haigrid.media.util.Digests;
import com.haigrid.media.util.Encodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/5/24 15:48
 */
@Slf4j
public class UserRealm extends AuthorizingRealm {
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * @description 登录认证信息
     * @author  zhengyanchuang
     * @date    2023/5/24 15:52
     * @param	authenticationToken
     * @return  org.apache.shiro.authc.AuthenticationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.error("进入认证.......");
        //获取用户账号
        String principal = (String) authenticationToken.getPrincipal();

        //通过用户名获取到用户信息
        SysUser sysUser = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getLoginName, principal));
        if (ObjectUtils.isEmpty(sysUser)){
            return null;
        }
        byte[] salt = Encodes.decodeHex(sysUser.getPassword().substring(0,16));
        return new SimpleAuthenticationInfo(sysUser,sysUser.getPassword().substring(16), ByteSource.Util.bytes(salt),getName());
    }
    /**
     * @description 授权信息
     * @author  zhengyanchuang
     * @date    2023/5/24 15:52
     * @param	principals
     * @return  org.apache.shiro.authz.AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        SysUser principal = (SysUser)principals.getPrimaryPrincipal();
        //获取登录用户
        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getLoginName, principal.getLoginName()));
        if (user != null) {
            List<SysMenu> list = sysMenuService.list(new LambdaQueryWrapper<>());
            for (SysMenu menu : list){
                if (StringUtils.isNotBlank(menu.getPermission())){
                    // 添加基于Permission的权限信息
                    for (String permission : StringUtils.split(menu.getPermission(),",")){
                        info.addStringPermission(permission);
                    }
                }
            }
            // 添加用户权限
            info.addStringPermission("user");
            //获取用户角色
            List<SysUserRole> list1 = this.sysUserRoleService.list(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId()));
            if(CollectionUtils.isEmpty(list1)){
                return info;
            }
            Collection<SysRole> sysRoles = this.sysRoleService.listByIds(list1.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()));
            // 添加用户角色信息
            for (SysRole role : sysRoles){
                info.addRole(role.getEnname());
            }
            return info;
        } else {
            return null;
        }
    }
}
