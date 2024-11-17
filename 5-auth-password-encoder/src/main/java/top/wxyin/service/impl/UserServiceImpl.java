package top.wxyin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.wxyin.entity.User;
import top.wxyin.mapper.UserMapper;
import top.wxyin.service.IUserService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wxyin
 * @since 2024-11-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}