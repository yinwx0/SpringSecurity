package top.wxyin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.wxyin.entity.User;
import top.wxyin.service.IUserService;

@SpringBootTest
@Slf4j
class ApplicationTests {
    @Resource
    private IUserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    // 可以修改为 "bcrypt" 、"argon2" 、"MD5" 、"sha256" 等方式
    String encodingId = "MD5";

    @Test
    @DisplayName("根据用户名查询用户")
    void testMp() {
        User admin = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUserName, "admin"));
        log.info(String.valueOf(admin));
    }

    //@Test
    //@DisplayName("插入一条用户数据")
    //void insertUserTest() {
    //    User user = new User();
    //    user.setUserName("admin1");
    //    user.setPassword(new BCryptPasswordEncoder().encode("123456"));
    //    user.setLoginName("管理员");
    //    user.setPhone("13688888888");
    //    userService.save(user);
    //}

    @Test
    @DisplayName("插入用户数据")
    void insertUserTest() {
        User user = new User();
        user.setUserName("bcrypt");
        // 使用 bcrypt 加密
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user.setLoginName("bcrypt");
        user.setPhone("13688888888");
        userService.save(user);

        User user2 = new User();
        user2.setUserName("argon2");
        // 使用 argon2 加密
        Argon2PasswordEncoder arg2SpringSecurity = new Argon2PasswordEncoder(16, 32, 1, 65536, 10);
        user2.setPassword(arg2SpringSecurity.encode("123456"));
        user2.setLoginName("argon2");
        user2.setPhone("13699999999");
        userService.save(user2);
    }

    @Test
    @DisplayName("根据手机号和密码登录")
    void loginTest() {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, "13688888888"));
        if ("$2a$10$eiX3TmNpA04B/Z6ZlapG8ucyuhV7b3XUyTdgZWtteqIbbB0HGijbu".equals(user.getPassword())) {
            log.info("登录成功");
        } else {
            log.info("登录失败");
        }
    }

    @Test
    @DisplayName("根据加密方式查询用户")
    void passwordEncodeTest() {
        User admin = userService.getOne(new LambdaQueryWrapper<User>().like(User::getPassword, "{argon2}"));
        if (admin != null) {
            log.info(String.valueOf(admin));
        } else {
            log.info("没找到");
        }
    }

    @Test
    @DisplayName("新增用户，密码添加算法前缀")
    void insertUserPasswordEncoder() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = new User();
        user.setUserName("zhangsan");
        // 使用加密：{bcrypt} 中的算法，可以从配置文件读取
        user.setPassword( passwordEncoder.encode("{bcrypt}123456"));
        user.setLoginName("zhangsan");
        user.setPhone("13911112222");
        userService.save(user);
    }

    @Test
    @DisplayName("新增用户，注入 PasswordEncoder")
    void insertUserPasswordEncoder1() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User user = new User();
        user.setUserName("lisi");
        user.setPassword("123456");
        user.setPassword( passwordEncoder.encode("123456"));
        user.setLoginName("lisi");
        user.setPhone("13911113333");
        userService.save(user);
    }

    @Test
    @DisplayName("新增用户，注入 PasswordEncoder")
    void insertUserPasswordEncoder2() {
        User user = new User();
        user.setUserName("mqxu");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setLoginName("mqxu");
        userService.save(user);
    }

}