package com.valdeslav.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity<Long> {

    @Column(name = "token_value", nullable = false)
    private String value;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    @OneToOne
    @JoinColumn(name = "user_info_id", referencedColumnName = "id")
    private User user;
}
