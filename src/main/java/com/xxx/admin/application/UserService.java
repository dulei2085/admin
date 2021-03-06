package com.xxx.admin.application;

import com.xxx.admin.domain.mapper.UserMbMapper;
import com.xxx.admin.domain.modle.User;
import com.xxx.admin.domain.modle.UserMb;
import com.xxx.admin.domain.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import com.xxx.admin.domain.modle.SelectRole;
import com.xxx.admin.domain.repository.RoleRepository;
import com.xxx.admin.domain.service.RoleSelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * Created by xiexx on 2016/9/17.
 */
@Service
@CacheConfig(cacheNames = "user")
public class UserService {

//    @Autowired
//    protected UserRepository userRepository;
    @Autowired
    protected UserMbMapper userRepository;

    @Autowired
    protected Md5PasswordEncoder md5PasswordEncoder;

    @Autowired
    protected RoleSelectService roleSelectService;

    @Autowired
    protected RoleRepository roleRepository;


    @Caching(
            put = @CachePut(key = "#user.id"),
            evict = @CacheEvict(value = "user-list", key = "'list'")
    )
    public UserMb create(UserMb user) {
        validate(user);
        Assert.hasText(user.getPassword());
        user.setDisabled(false);
        user.setCreateTime(new Date());
        user.setSalt(RandomStringUtils.randomAscii(10));
        user.setPassword(md5PasswordEncoder.encodePassword(user.getPassword(), user.getSalt()));
        userRepository.add(user);
        return user;
    }


    @Caching(
            put = @CachePut(key = "#user.id"),
            evict = @CacheEvict(value = "user-list", key = "'list'")
    )
    public UserMb modify(UserMb user) {
        Assert.hasText(user.getId());
        UserMb old = get(user.getId());
        if (StringUtils.isNotBlank(user.getUsername())) {
            old.setUsername(user.getUsername());
        }
        if (StringUtils.isNotBlank(user.getPassword())) {
            old.setPassword(md5PasswordEncoder.encodePassword(user.getPassword(), old.getSalt()));
        }
        if (StringUtils.isNotBlank(user.getEmail())) {
            old.setEmail(user.getEmail());
        }
        userRepository.update(old);
        return user;
    }

    @Caching(
            evict = {@CacheEvict(value = "user-list", key = "'list'"), @CacheEvict(key = "#id")}
    )
    public void delete(String id) {
        userRepository.remove(id);
    }

    @Cacheable
    public UserMb get(String id) {
        return userRepository.get(id);
    }

    @Cacheable(value = "user-list", key = "'list'")
    public List<UserMb> list() {
        return userRepository.list();
    }

    @Caching(
            evict = {@CacheEvict(value = "user-list", key = "'list'"), @CacheEvict(key = "#id")}
    )
    public void switchStatus(String id, boolean disable) {
        userRepository.switchStatus(id, disable);
    }



    @Caching(
            evict = {@CacheEvict(value = "user-nav-menu", key = "#uid")}
    )
    public void grantRole(String uid, List<String> roleIds) {
        userRepository.updateRoles(uid, roleIds);
    }


    private void validate(UserMb user) {
        Assert.hasText(user.getUsername());
        if (user.isRoot()) {
            throw new IllegalArgumentException("user loginName cannot is root");
        }
    }

    public List<SelectRole> selectRoles(String uid) {
        return roleSelectService.mergeRole(roleRepository.list(), roleRepository.getRoles(uid));
    }

}
