package com.zl.mjga.repository;

import org.jooq.Configuration;
import org.jooq.generated.mjga.tables.daos.LibraryDocSegmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LibraryDocSegmentRepository extends LibraryDocSegmentDao {
  @Autowired
  public LibraryDocSegmentRepository(Configuration configuration) {
    super(configuration);
  }
}
