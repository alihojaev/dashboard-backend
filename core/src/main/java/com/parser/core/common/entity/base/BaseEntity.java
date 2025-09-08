package com.parser.core.common.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedBy
    @Column(updatable = false)
    UUID createdBy;

    @LastModifiedBy
    UUID modifiedBy;

    @CreatedDate
    @Column(updatable = false)
    LocalDateTime cdt;

    @LastModifiedDate
    @Column()
    LocalDateTime mdt;

    @Column(name = "rdt")
    LocalDateTime rdt;

    public void markRemoved() {
        rdt = LocalDateTime.now();
    }
}
