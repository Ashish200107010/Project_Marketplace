package com.certplatform.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.certplatform.user.entity.Student;
import com.certplatform.user.repository.StudentRepository;

import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public List<String> updateRoles(UUID studentId, List<String> roles) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setRolesLookingFor(roles);
        studentRepository.save(student);
        return student.getRolesLookingFor();
    }

    @Transactional
    public List<String> updateSkills(UUID studentId, List<String> skills) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        student.setSkills(skills);
        studentRepository.save(student);
        return student.getSkills();
    }
}
