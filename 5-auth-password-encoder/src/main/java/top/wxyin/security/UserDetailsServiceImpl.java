package top.wxyin.security;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.wxyin.entity.User;
import top.wxyin.service.IUserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 数据库查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUserName, username));
        if (ObjectUtil.isNull(user)) {
            log.error("Query returned no results for user {}", username);
            throw new UsernameNotFoundException(StrUtil.format("Username {} not found", username));
        } else {
            // 2. 设置权限集合，后续需要从数据库查询
            List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList("role");
            // 3. 返回 UserDetails 类型用户，账号状态这里都直接设置为启用，实际业务可以存在数据库中
            return new MyUserDetails(username, user.getPassword(), user.getPhone(), authorityList, true, true, true, true);
        }
    }
}