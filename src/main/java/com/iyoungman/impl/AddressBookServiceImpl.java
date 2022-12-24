package com.iyoungman.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iyoungman.entity.AddressBook;
import com.iyoungman.mapper.AddressBookMapper;
import com.iyoungman.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author Blockbuster
 * @date 2022/4/20 21:48:29 星期三
 */

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
