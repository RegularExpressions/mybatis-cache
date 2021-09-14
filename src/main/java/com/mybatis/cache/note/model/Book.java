package com.mybatis.cache.note.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Book implements Serializable {
  private static final long serialVersionUID = 7435841739728908968L;
  private long id;
  private String bookName;
}
