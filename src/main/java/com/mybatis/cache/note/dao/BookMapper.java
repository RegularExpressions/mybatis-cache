package com.mybatis.cache.note.dao;

import com.mybatis.cache.note.model.Book;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.impl.PerpetualCache;

/**
 * @CacheNamespace 相当于 对应mapper.xml 中的<cache/>
 * eviction : 缓存回收策略
 *    LRU -- 最近最少使用，移除最长时间不被使用的对象
 *    FIFO -- 先进先出，按照对象进入缓存的顺序来移除
 *    默认为LRU
 * flushInterval : 刷新间隔，单位毫秒
 *    默认情况不设置，也就是没有刷新间隔，缓存仅调用语句时刷新
 * size : 缓存最大数量
 *    代表缓存最多可以存储多少个对象，太大容易导致oom
 * readWrite 读 写缓存
 *   false ： 只读缓存 会给所有调用者返回缓存对象的相同实例(引用)，因此这些对象理应不被修改
 *   true  ： 读 写缓存 回返回缓存对象的拷贝(序列化反序列化克隆)，会有性能损耗，但是安全 ，readWrite默认为true
 */
@Mapper
@CacheNamespace(implementation = PerpetualCache.class,eviction = LruCache.class,flushInterval = 0L,size = 2048,readWrite = true)
public interface BookMapper {

  @Select("select id,bookName from t_book where id = #{id}")
  @Options(useCache = false)
  Book getBookById(@Param("id")long id);

  @Select("select id,bookName from t_book where id = #{id}")
  Book getBookByIdCp(@Param("id")long id);

  @Update("update t_book set bookName = #{name} where id = #{id}")
  void updateById(@Param("name") String bookName,@Param("id")long id);
}
