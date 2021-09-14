package com.mybatis.cache.note;

import com.mybatis.cache.note.dao.BookMapper;
import com.mybatis.cache.note.model.Book;
import org.apache.ibatis.session.SqlSession;
import org.springframework.util.Assert;

public class Main {
  public static void main(String[] args) throws Exception {

//    expireFirstMybatisCache();
    testSecondMybatisCache();
  }

  /**
   * 体验一级缓存(sqlSession级别，一级缓存默认是一直开启的)
   *   与数据库同一次会话（sqlSession）期间查询到的数据会放到本地缓存中。
   *   后续如果需要获取相同得数据，直接从缓存取，不再去查询数据库
   */
  private static void testFirstMybatisCache(){
    SqlSession sqlSession1 = null;

    try {
      sqlSession1 = SqlSessionFactoryUtil.openSession();
      BookMapper bookMapper = sqlSession1.getMapper(BookMapper.class);

      Book admin1 = bookMapper.getBookById(1);
      System.out.println(admin1);
      Book admin2 = bookMapper.getBookById(1);
      System.out.println(admin2);
//      sqlSession.commit();
      System.out.println("查询实体是否相等："+ (admin1 == admin2));

    } catch (Exception e) {
      System.out.println(e.getMessage());
      if(sqlSession1 != null) sqlSession1.rollback();
    } finally {
      if (sqlSession1 != null) sqlSession1.close();
    }
  }

  /**
   * 一级缓存失效情况（即不走缓存去查库得情况）：
   *   1.配置关闭一级缓存时，相关配置 localCacheScope = STATEMENT（https://mybatis.org/mybatis-3/configuration.html#settings）
   *   2.SqlSession不同
   *   3.SqlSession相同，查询条件不同
   *   3.SqlSession相同,查询条件相同，两次查询之间执行了增删改操作
   *   4.手动清除了一级缓存
   */
  private static void expireFirstMybatisCache(){
    SqlSession sqlSession1 = null;
    SqlSession sqlSession2 = null;

    try {
      sqlSession1 = SqlSessionFactoryUtil.openSession();
      sqlSession2 = SqlSessionFactoryUtil.openSession();

      //sqlSessionDiff(sqlSession1, sqlSession2);
      //sameSqlSessionButConditionDiff(sqlSession1);
      //sameSessionAndConditionButCUD(sqlSession1);
      clearCacheDemo(sqlSession1);

    }catch (Exception e){
      System.out.println(e.getMessage());
      if(sqlSession2 != null) sqlSession2.rollback();
      if(sqlSession1 != null) sqlSession1.rollback();
    }finally {
      if(sqlSession2 != null) sqlSession2.close();
      if (sqlSession1 != null) sqlSession1.close();
    }
  }


  /**
   * 体验mybatis二级缓存（全局缓存）：基于namespace级别的缓存，一个namespace对应一个二级缓存
   *    如何使用：
   *    1.开启全局配置 <setting name="cacheEnabled" value="true"/>
   *    2.对应mapper使用@CacheNamespace注解开启，mapper.xml中使用<cache/>开启
   *    3.要缓存的实体类实现序列化接口
   *   工作机制：
   *   1。一个会话，查询一条数据，这个数据就会被放在对应SqlSession的一级缓存中；
   *   2.如果会话关闭，一级缓存的数据会被保存在二级缓存中；
   *
   *   和二级缓存有关的设置：
   *   cacheEnabled = true /false (开启关闭二级缓存)
   *   每个select标签都有useCache = true
   *   每个增删改标签的flushCache = true（增删改执行完成后就会清除缓存,一二级都会清除）
   */
  private static void testSecondMybatisCache(){
    SqlSession sqlSession1 = null;
    SqlSession sqlSession2 = null;

    try {
      sqlSession1 = SqlSessionFactoryUtil.openSession();
      sqlSession2 = SqlSessionFactoryUtil.openSession();

      BookMapper bookMapper1 = sqlSession1.getMapper(BookMapper.class);
      BookMapper bookMapper2 = sqlSession2.getMapper(BookMapper.class);

      Book book1 = bookMapper1.getBookByIdCp(1);
      sqlSession1.commit();
//      bookMapper2.updateById("python",2);
      Book book2 = bookMapper2.getBookByIdCp(1);
      System.out.println(book1 == book2);
    }catch (Exception e){
      System.out.println(e.getMessage());
      if(sqlSession2 != null) sqlSession2.rollback();
      if(sqlSession1 != null) sqlSession1.rollback();
    }finally {
      if(sqlSession2 != null) sqlSession2.close();
      if (sqlSession1 != null) sqlSession1.close();
    }
  }

  private static void clearCacheDemo(SqlSession sqlSession1) {
    BookMapper bookMapper1 = sqlSession1.getMapper(BookMapper.class);
    Book book1 = bookMapper1.getBookById(1);
    System.out.println(book1);

    sqlSession1.clearCache();

    Book book2 = bookMapper1.getBookById(1);
    System.out.println(book2);
    System.out.println(book1 == book2);
  }

  /**
   * SqlSession相同,查询条件相同，两次查询之间执行了增删改操作
   * @param sqlSession1
   */
  private static void sameSessionAndConditionButCUD(SqlSession sqlSession1) {
    BookMapper bookMapper1 = sqlSession1.getMapper(BookMapper.class);
    Book book1 = bookMapper1.getBookById(1);
    System.out.println(book1);
    bookMapper1.updateById("python",2);
    Book book2 = bookMapper1.getBookById(1);
    System.out.println(book2);
    System.out.println(book1 == book2);
  }

  /**
   * SqlSession相同，查询条件不同
   * @param sqlSession1
   */
  private static void sameSqlSessionButConditionDiff(SqlSession sqlSession1) {
    BookMapper bookMapper1 = sqlSession1.getMapper(BookMapper.class);
    Book book1 = bookMapper1.getBookById(1);
    System.out.println(book1);
    Book book2 = bookMapper1.getBookById(2);
    System.out.println(book2);
    System.out.println(book1 == book2);
  }

  /**
   * sqlSession不同时一级缓存失效，意味着证实一级缓存与sqlSession挂钩，是sqlSession级别的
   * @param sqlSession1
   * @param sqlSession2
   */
  private static void sqlSessionDiff(SqlSession sqlSession1, SqlSession sqlSession2) {
    BookMapper bookMapper1 = sqlSession1.getMapper(BookMapper.class);
    BookMapper bookMapper2 = sqlSession2.getMapper(BookMapper.class);

    Book book1 = bookMapper1.getBookById(1);
    System.out.println(book1);
    Book book2 = bookMapper2.getBookById(1);
    System.out.println(book2);
    System.out.println(book1 == book2);
  }

}

