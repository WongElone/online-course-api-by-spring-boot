package com.elonewong.onlinecourseapi.csr.teacher;

import com.elonewong.onlinecourseapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private TeacherRepository teacherRepository;
    private TeacherResponseMapper teacherResponseMapper;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, TeacherResponseMapper teacherResponseMapper) {
        this.teacherRepository = teacherRepository;
        this.teacherResponseMapper = teacherResponseMapper;
    }

    public List<TeacherResponse> getAllTeachers() {
        return teacherRepository.findAll().stream().map(teacherResponseMapper).collect(Collectors.toList());
    }

    public TeacherResponse getOneTeacher(String id) {
        return teacherResponseMapper.apply(findTeacherById(id));
    }

    public Teacher findTeacherById(String teacherId) {
        return teacherRepository.findById(teacherId).orElseThrow(() -> new ResourceNotFoundException("Teacher id not found"));
    }

}
