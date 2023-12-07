package com.elonewong.onlinecourseapi.csr.student;

import com.elonewong.onlinecourseapi.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private StudentRepository studentRepository;
    private StudentResponseMapper studentResponseMapper;

    @Autowired
    public StudentService(StudentRepository studentRepository, StudentResponseMapper studentResponseMapper) {
        this.studentRepository = studentRepository;
        this.studentResponseMapper = studentResponseMapper;
    }

    public StudentResponse getOneStudent(String id) {
        return studentResponseMapper.apply(findStudentById(id));
    }

    public Student findStudentById(String studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("student id not found"));
    }

}
