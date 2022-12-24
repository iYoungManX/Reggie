package com.iyoungman.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iyoungman.entity.Category;

/**
 * @author Blockbuster
 * @date 2022/4/18 20:48:20 星期一
 */


public interface CategoryService extends IService<Category> {

    void remove(Long ids);
}
