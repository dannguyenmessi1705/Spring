package com.didan.testperformance.first.entity;

import com.didan.testperformance.first.constant.ProtocolTypeEnum;
import com.didan.testperformance.first.constant.RequestTypeEnum;
import com.didan.testperformance.first.util.DateUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "logs_info")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogsInfoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "request_id", length = 50, nullable = false)
  private String requestId;

  @Column(name = "protocol_type", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private ProtocolTypeEnum type;

  @Column(name = "request_type", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  private RequestTypeEnum requestType;

  @Column(name = "message", nullable = false)
  @Lob
  private String message;

  @Column(name = "created_at", nullable = false)
  private Date created_at;

  @PrePersist
  public void getNow() {
    this.created_at = new Date();
    this.requestId = DateUtil.dateToString(created_at, DateUtil.YYYYMMDDHHMMSSSSS);
  }
}
