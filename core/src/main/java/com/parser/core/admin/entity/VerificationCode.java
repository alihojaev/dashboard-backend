package com.parser.core.admin.entity;

import com.parser.core.util.annotation.sql.SqlTable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = VerificationCode.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationCode {

    @SqlTable
    public static final String TABLE_NAME = "verification_codes";
    public static final String SEQ_NAME = TABLE_NAME + "_SEQ";

    @Id
    @GeneratedValue(generator = "system-uuid")
    UUID id;

    @Column(nullable = false)
    String code;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private AdminEntity user;
} 