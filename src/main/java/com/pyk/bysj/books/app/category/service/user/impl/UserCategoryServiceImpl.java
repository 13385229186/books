package com.pyk.bysj.books.app.category.service.user.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pyk.bysj.books.app.category.service.user.UserCategoryService;
import com.pyk.bysj.books.mapper.CategoryMapper;
import com.pyk.bysj.books.model.entity.BookNumber;
import com.pyk.bysj.books.model.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserCategoryServiceImpl implements UserCategoryService {
  private final CategoryMapper categoryMapper;

  public UserCategoryServiceImpl(CategoryMapper categoryMapper){
    this.categoryMapper = categoryMapper;
  }

  @Override
  public List<Category> categoriList() {
    return categoryMapper.selectList(
            Wrappers.<Category>lambdaQuery()
                    .select(Category::getId, Category::getName)
    );
  }
}
