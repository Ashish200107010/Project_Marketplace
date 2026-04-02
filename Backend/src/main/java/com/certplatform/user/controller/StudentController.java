package com.certplatform.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.certplatform.user.service.StudentService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<Map<String, List<String>>> updateRoles(
            @PathVariable("id") UUID id,
            @RequestBody Map<String, List<String>> request) {
        List<String> updatedRoles = studentService.updateRoles(id, request.get("roles"));
        return ResponseEntity.ok(Map.of("roles", updatedRoles));
    }

    @PutMapping("/{id}/skills")
    public ResponseEntity<Map<String, List<String>>> updateSkills(
            @PathVariable("id") UUID id,
            @RequestBody Map<String, List<String>> request) {
        List<String> updatedSkills = studentService.updateSkills(id, request.get("skills"));
        return ResponseEntity.ok(Map.of("skills", updatedSkills));
    }
}
