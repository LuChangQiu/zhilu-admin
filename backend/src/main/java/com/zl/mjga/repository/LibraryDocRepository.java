package com.zl.mjga.repository;

import org.jooq.Configuration;
import org.jooq.generated.mjga.tables.daos.LibraryDocDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LibraryDocRepository extends LibraryDocDao {
  @Autowired
  public LibraryDocRepository(Configuration configuration) {
    super(configuration);
  }
}
