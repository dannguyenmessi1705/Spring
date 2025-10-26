package com.didan.logquickwit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "log_request", columnDefinition = "TEXT")
    @Lob
    private String logRequest;

    @Column(name = "log_response", columnDefinition = "TEXT")
    @Lob
    private String logResponse;

}
