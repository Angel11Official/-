package org.reggieapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.reggieapp.entity.Category;

public interface CategoryService extends IService<Category> {
    //扩展
    public void remove(Long id);
}
