package com.zl.mjga.dto.urp;

import java.time.OffsetDateTime;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserQueryDto {
  private String username;
  private OffsetDateTime startDate;
  private OffsetDateTime endDate;
}
