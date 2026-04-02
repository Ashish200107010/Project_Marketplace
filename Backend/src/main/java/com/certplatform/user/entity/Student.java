package com.certplatform.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

import com.certplatform.common.entity.BaseUser;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseUser {

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String college;

    @Column(name = "mobile_no", nullable = true)
    private String mobileNo;

    @ElementCollection
    @CollectionTable(name = "student_skills", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "skills")
    private List<String> skills;

    @ElementCollection
    @CollectionTable(name = "student_roles_looking_for", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "roles_looking_for", nullable = true)
    private List<String> rolesLookingFor;
}
