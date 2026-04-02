package com.certplatform.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.certplatform.common.entity.BaseUser;


@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends BaseUser {

    @Column(nullable = true)
    private String name;

    @Column(name = "mobile_no", nullable = true)
    private String mobileNo;
}
