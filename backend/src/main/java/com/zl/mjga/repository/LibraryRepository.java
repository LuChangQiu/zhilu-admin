package com.zl.mjga.repository;

import org.jooq.Configuration;
import org.jooq.generated.mjga.tables.daos.LibraryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LibraryRepository extends LibraryDao {

  @Autowired
  public LibraryRepository(Configuration configuration) {
    super(configuration);
  }
}
