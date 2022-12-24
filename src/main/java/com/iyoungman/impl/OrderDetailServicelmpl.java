package com.iyoungman.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iyoungman.entity.OrderDetail;
import com.iyoungman.mapper.OrderDetailMapper;
import com.iyoungman.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author Blockbuster
 * @date 2022/4/22 14:31:48 星期五
 */

@Service
public class OrderDetailServicelmpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
