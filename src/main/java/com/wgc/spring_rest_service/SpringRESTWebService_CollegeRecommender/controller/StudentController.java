//package com.wgc.spring_rest_service.SpringRESTWebService_CollegeRecommender.controller;
//
//
//import com.wgc.spring_rest_service.SpringRESTWebService_CollegeRecommender.db.AppUserDao;
//import com.wgc.spring_rest_service.SpringRESTWebService_CollegeRecommender.entity.AppUser;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@CrossOrigin("*")
//@RequestMapping("/student")
//public class StudentController {
//    @Autowired
//    private AppUserDao appUserDao;
//
//    @GetMapping("/{id}")
//    public AppUser getStudentById(@PathVariable int id) {
//        return appUserDao.getAppUserById(id);
//    }
//
//    public boolean addStudent(AppUser appUser) {
//        return true;
//    }
//
//    public boolean updateStudent(AppUser appUser) {
//        return true;
//    }
//
//}