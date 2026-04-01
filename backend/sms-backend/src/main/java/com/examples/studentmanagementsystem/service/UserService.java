package com.examples.studentmanagementsystem.service;

import com.examples.studentmanagementsystem.dto.request.CreateUserRequest;
import com.examples.studentmanagementsystem.entity.User;

public interface UserService {

    User createUser(CreateUserRequest request);
}