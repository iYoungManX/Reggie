package com.iyoungman.dto;

import com.iyoungman.entity.Dish;
import com.iyoungman.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    // 存放口味表 是一个 DishFlavor 的集合
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
