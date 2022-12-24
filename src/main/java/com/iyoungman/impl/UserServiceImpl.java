package com.iyoungman.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iyoungman.entity.User;
import com.iyoungman.mapper.UserMapper;
import com.iyoungman.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Blockbuster
 * @date 2022/4/21 11:38:34 星期四
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
