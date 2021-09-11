package com.mybatis.cache.note;

import com.mybatis.cache.note.dao.BookMapper;
import com.mybatis.cache.note.model.Book;
import org.apache.ibatis.session.SqlSession;

public class Main {
  public static void main(String[] args) throws Exception {

    testFirstMybatisCache();
  }

  private static void testFirstMybatisCache(){
    SqlSession sqlSession = null;
    SqlSession sqlSession2=null;

    try {
      sqlSession = SqlSessionFactoryUtil.openSession();
      sqlSession2 = SqlSessionFactoryUtil.openSession();
      BookMapper bookMapper = sqlSession.getMapper(BookMapper.class);
      BookMapper bookMapper2 = sqlSession2.getMapper(BookMapper.class);


      Book admin = bookMapper.getBookById(1);
      System.out.println(admin);
      Book admin1 = bookMapper.getBookById(1);
//      sqlSession.commit();

    } catch (Exception e) {
      System.out.println(e);
      sqlSession.rollback();
    } finally {
      if (sqlSession != null) {
        sqlSession.close();
      }
    }
  }

}

