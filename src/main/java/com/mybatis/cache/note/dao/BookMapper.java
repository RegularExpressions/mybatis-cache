package com.mybatis.cache.note.dao;

import com.mybatis.cache.note.model.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BookMapper {

  @Select("select id,bookName from t_book where id = #{id}")
  Book getBookById(@Param("id")long id);
}
