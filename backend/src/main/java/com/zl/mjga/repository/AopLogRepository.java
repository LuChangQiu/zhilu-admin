package com.zl.mjga.repository;

import static org.jooq.generated.mjga.tables.AopLog.AOP_LOG;
import static org.jooq.generated.mjga.tables.User.USER;
import static org.jooq.impl.DSL.*;

import com.zl.mjga.dto.PageRequestDto;
import com.zl.mjga.dto.aoplog.AopLogQueryDto;
import java.time.OffsetDateTime;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.generated.mjga.tables.daos.AopLogDao;
import org.jooq.generated.mjga.tables.pojos.AopLog;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** AOP日志Repository */
@Repository
public class AopLogRepository extends AopLogDao {

  @Autowired
  public AopLogRepository(Configuration configuration) {
    super(configuration);
  }

  public Result<Record> pageFetchBy(PageRequestDto pageRequestDto, AopLogQueryDto queryDto) {
    return selectByWithoutReturnValue(queryDto)
        .orderBy(pageRequestDto.getSortFields())
        .limit(pageRequestDto.getSize())
        .offset(pageRequestDto.getOffset())
        .fetch();
  }

  public List<AopLog> fetchBy(AopLogQueryDto queryDto) {
    return selectBy(queryDto).fetchInto(AopLog.class);
  }

  public SelectConditionStep<Record> selectBy(AopLogQueryDto queryDto) {
    return ctx()
        .select(AOP_LOG.asterisk(), USER.USERNAME, DSL.count().over().as("total_count"))
        .from(AOP_LOG)
        .leftJoin(USER)
        .on(AOP_LOG.USER_ID.eq(USER.ID))
        .where(buildConditions(queryDto));
  }


  public SelectConditionStep<Record> selectByWithoutReturnValue(AopLogQueryDto queryDto) {
    return ctx()
            .select(AOP_LOG.asterisk().except(AOP_LOG.RETURN_VALUE, AOP_LOG.METHOD_ARGS), USER.USERNAME, DSL.count().over().as("total_count"))
            .from(AOP_LOG)
            .leftJoin(USER)
            .on(AOP_LOG.USER_ID.eq(USER.ID))
            .where(buildConditions(queryDto));
  }

  private Condition buildConditions(AopLogQueryDto queryDto) {
    Condition condition = noCondition();

    if (queryDto == null) {
      return condition;
    }

    // ID精确查询
    if (queryDto.getId() != null) {
      condition = condition.and(AOP_LOG.ID.eq(queryDto.getId()));
    }

    // 类名模糊查询
    if (StringUtils.isNotBlank(queryDto.getClassName())) {
      condition = condition.and(AOP_LOG.CLASS_NAME.like("%" + queryDto.getClassName() + "%"));
    }

    // 方法名模糊查询
    if (StringUtils.isNotBlank(queryDto.getMethodName())) {
      condition = condition.and(AOP_LOG.METHOD_NAME.like("%" + queryDto.getMethodName() + "%"));
    }

    // 成功状态
    if (queryDto.getSuccess() != null) {
      condition = condition.and(AOP_LOG.SUCCESS.eq(queryDto.getSuccess()));
    }

    // 用户ID
    if (queryDto.getUserId() != null) {
      condition = condition.and(AOP_LOG.USER_ID.eq(queryDto.getUserId()));
    }

    // IP地址模糊查询
    if (StringUtils.isNotBlank(queryDto.getIpAddress())) {
      condition = condition.and(AOP_LOG.IP_ADDRESS.like("%" + queryDto.getIpAddress() + "%"));
    }

    // 时间范围查询
    if (queryDto.getStartTime() != null) {
      OffsetDateTime startTime = queryDto.getStartTime().atOffset(OffsetDateTime.now().getOffset());
      condition = condition.and(AOP_LOG.CREATE_TIME.ge(startTime));
    }

    if (queryDto.getEndTime() != null) {
      OffsetDateTime endTime = queryDto.getEndTime().atOffset(OffsetDateTime.now().getOffset());
      condition = condition.and(AOP_LOG.CREATE_TIME.le(endTime));
    }

    // 执行时间范围
    if (queryDto.getMinExecutionTime() != null) {
      condition = condition.and(AOP_LOG.EXECUTION_TIME.ge(queryDto.getMinExecutionTime()));
    }

    if (queryDto.getMaxExecutionTime() != null) {
      condition = condition.and(AOP_LOG.EXECUTION_TIME.le(queryDto.getMaxExecutionTime()));
    }

    return condition;
  }

  public int deleteByIds(List<Long> ids) {
    return ctx().deleteFrom(AOP_LOG).where(AOP_LOG.ID.in(ids)).execute();
  }

  public int deleteBeforeTime(OffsetDateTime beforeTime) {
    return ctx().deleteFrom(AOP_LOG).where(AOP_LOG.CREATE_TIME.lt(beforeTime)).execute();
  }
}
