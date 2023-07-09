package org.reggieapp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggieapp.entity.AddressBook;
import org.reggieapp.mapper.AddressBookMapper;
import org.reggieapp.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
