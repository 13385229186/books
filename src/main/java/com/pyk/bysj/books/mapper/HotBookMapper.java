package com.pyk.bysj.books.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pyk.bysj.books.model.entity.HotBook;

import java.util.List;

public interface HotBookMapper extends BaseMapper<HotBook> {
  Integer batchInsertOrUpdateByBookId(List<HotBook> hotBooks);
}
