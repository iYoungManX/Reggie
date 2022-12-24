package com.iyoungman.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iyoungman.entity.ShoppingCart;
import com.iyoungman.mapper.ShoppingCartMapper;
import com.iyoungman.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author Blockbuster
 * @date 2022/4/21 21:46:32 星期四
 */

@Service
public class ShoppingCartServicelmpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
