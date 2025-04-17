package com.pyk.bysj.books.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import com.pyk.bysj.books.enums.BorrowStatus;
import com.pyk.bysj.books.model.entity.Borrow;
import com.pyk.bysj.books.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface BorrowMapper extends BaseMapper<Borrow>, MPJBaseMapper<Borrow> {
  // 自定义批量更新状态
  @Update("UPDATE user SET status = #{status} WHERE id IN (#{ids})")
  int updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") BorrowStatus status);
}
