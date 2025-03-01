package com.iyoungman.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iyoungman.common.CustomException;
import com.iyoungman.common.R;
import com.iyoungman.dto.SetmealDishDto;
import com.iyoungman.dto.SetmealDto;
import com.iyoungman.entity.Category;
import com.iyoungman.entity.Dish;
import com.iyoungman.entity.Setmeal;
import com.iyoungman.entity.SetmealDish;
import com.iyoungman.service.CategoryService;
import com.iyoungman.service.DishService;
import com.iyoungman.service.SetmealDishService;
import com.iyoungman.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Blockbuster
 * @date 2022/4/20 14:16:52 星期三
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private DishService dishService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "SetmealCache", key = "#p0.categoryId + '_' + #p0.status")
    public R<String> addsetmeal(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);

        return R.success("添加套餐成功");
    }

    /**
     * 分页查询，根据 id 查询套餐
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @Transactional
    // 参考 DishController 里的分页过程
    public R<Page> setmealpage(int page, int pageSize, String name){
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);

        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(name != null, Setmeal::getName, name);

        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(setmealPage, setmealLambdaQueryWrapper);

        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        List<Setmeal> setmealList = setmealPage.getRecords();


        List<SetmealDto> setmealDtoList = setmealList.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }else throw new CustomException("找不到套餐分类");

                return setmealDto;

        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐，批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "SetmealCache", key = "")
    public R<String> setmealdelete(@RequestParam List<Long> ids){

        setmealService.removeWithDish(ids);

        return R.success("删除成功");
    }

    /**
     * 停售起售及批量停售起售
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> change(@RequestParam List<Long> ids, @PathVariable int status){
        setmealService.statusChangeWithDish(ids,status);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        List<Setmeal> setmealList = setmealService.list(setmealLambdaQueryWrapper);
        setmealList.stream().map((item) -> {
            String key = "SetmealCache::" + item.getCategoryId() + "_1";
            redisTemplate.delete(key);
            return item;
        }).collect(Collectors.toList());

        return R.success("修改成功");
    }

    /**
     * 根据 id 查询套餐信息和对应的菜品信息，回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> list(@PathVariable Long id){
        return R.success(setmealService.getByIdWithDish(id));
    }

    /**
     * 点保存修改菜品
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "SetmealCache", key = "#p0.categoryId + '_' + #p0.status")
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateSetmealWithDish(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 根据分类id CategoryId，状态 status 查询菜品
     * @param categoryId
     * @param status
     * @return
     */
    @Cacheable(value = "SetmealCache", key = "#p0 + '_' + #p1")
    @GetMapping("list")
    public R<List<Setmeal>> list(@RequestParam Long categoryId, int status){

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != 0, Setmeal::getCategoryId, categoryId);
        wrapper.eq(status != 0, Setmeal::getStatus, status);
        wrapper.orderByAsc(Setmeal::getPrice).orderByDesc(Setmeal::getUpdateTime);

        return R.success(setmealService.list(wrapper));
    }


    /**
     * 点击套餐图片弹出窗口显示菜品及其图片
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    // 因为 SetmealDish 没有图片信息，所以新建一个 SetmealDishDto 新增图片参数，通过查询 Dish 表取出图片信息
    public R<List<SetmealDishDto>> setmeal(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(id != null, SetmealDish::getSetmealId, id);

        List<SetmealDish> setmealDishList = setmealDishService.list(wrapper);

        List<SetmealDishDto> setmealDishDtoList = setmealDishList.stream().map((item) -> {
            SetmealDishDto setmealDishDto = new SetmealDishDto();
            BeanUtils.copyProperties(item, setmealDishDto);
            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(Dish::getId, item.getDishId());
            setmealDishDto.setImage(dishService.getOne(dishLambdaQueryWrapper).getImage());
            return setmealDishDto;
        }).collect(Collectors.toList());

        return R.success(setmealDishDtoList);
    }


}
