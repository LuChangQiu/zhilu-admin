package com.zl.mjga.dto.urp;

import java.time.LocalDateTime;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserQueryDto {
  private String username;
  private LocalDateTime starDate;
  private LocalDateTime endDate;
}
