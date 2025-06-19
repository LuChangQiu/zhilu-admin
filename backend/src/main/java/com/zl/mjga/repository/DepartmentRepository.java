package com.zl.mjga.repository;

import static org.jooq.generated.mjga.Tables.*;
import static org.jooq.impl.DSL.*;
import static org.jooq.impl.SQLDataType.VARCHAR;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.department.DepartmentQueryDto;
import com.zl.mjga.dto.department.DepartmentWithParentDto;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.generated.mjga.tables.daos.DepartmentDao;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DepartmentRepository extends DepartmentDao {

  @Autowired
  public DepartmentRepository(Configuration configuration) {
    super(configuration);
  }

  public List<DepartmentWithParentDto> queryDepartmentAndSubsBy(Long id) {
    CommonTableExpression<?> cte =
        name("parent_department")
            .fields("id", "name", "parent_name", "parent_id", "path")
            .as(
                select(
                        DEPARTMENT.ID,
                        DEPARTMENT.NAME,
                        DEPARTMENT.NAME,
                        DEPARTMENT.PARENT_ID,
                        DEPARTMENT.NAME.cast(VARCHAR))
                    .from(DEPARTMENT)
                    .where(id == null ? noCondition() : DEPARTMENT.ID.eq(id))
                    .and(DEPARTMENT.PARENT_ID.isNull())
                    .unionAll(
                        select(
                                DEPARTMENT.ID,
                                DEPARTMENT.NAME,
                                field(name("parent_department", "name"), VARCHAR),
                                DEPARTMENT.PARENT_ID,
                                field(name("parent_department", "path"), VARCHAR)
                                    .concat("->")
                                    .concat(DEPARTMENT.NAME))
                            .from(table(name("parent_department")))
                            .join(DEPARTMENT)
                            .on(
                                field(name("parent_department", "id"), Long.class)
                                    .eq(DEPARTMENT.PARENT_ID))));
    return ctx().withRecursive(cte).selectFrom(cte).fetch().into(DepartmentWithParentDto.class);
  }

  public Result<Record> pageFetchBy(
      PageRequestDto pageRequestDto, DepartmentQueryDto departmentQueryDto) {
    org.jooq.generated.mjga.tables.Department parent = DEPARTMENT.as("parent");
    return ctx()
        .select(
            DEPARTMENT.asterisk(),
            parent.NAME.as("parent_name"),
            departmentQueryDto.getUserId() != null
                ? DSL.when(
                        DEPARTMENT.ID.in(selectUsersDepartment(departmentQueryDto.getUserId())),
                        true)
                    .otherwise(false)
                    .as("is_bound")
                : noCondition(),
            DSL.count().over().as("total_department").convertFrom(Long::valueOf))
        .from(DEPARTMENT)
        .leftJoin(parent)
        .on(parent.ID.eq(DEPARTMENT.PARENT_ID))
        .where(
            switch (departmentQueryDto.getBindState()) {
              case BIND -> DEPARTMENT.ID.in(selectUsersDepartment(departmentQueryDto.getUserId()));
              case UNBIND ->
                  DEPARTMENT.ID.notIn(selectUsersDepartment(departmentQueryDto.getUserId()));
              case ALL -> noCondition();
            })
        .and(
            StringUtils.isNotEmpty(departmentQueryDto.getName())
                ? DEPARTMENT.NAME.like("%" + departmentQueryDto.getName() + "%")
                : noCondition())
        .orderBy(pageRequestDto.getSortFields())
        .limit(pageRequestDto.getSize())
        .offset(pageRequestDto.getOffset())
        .fetch();
  }

  private SelectConditionStep<Record1<Long>> selectUsersDepartment(Long userId) {
    return DSL.select(USER.department().ID)
        .from(USER)
        .innerJoin(USER.department())
        .where(USER.ID.eq(userId));
  }

  @Transactional
  public void deleteByName(String name) {
    ctx().deleteFrom(DEPARTMENT).where(DEPARTMENT.NAME.eq(name)).execute();
  }
}
