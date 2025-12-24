package com.ecommerce.project.service;

import com.ecommerce.project.dto.UserRegisterDTO;
import com.ecommerce.project.dto.UserLoginDTO;
import com.ecommerce.project.dto.UserUpdateDTO;
import com.ecommerce.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    User register(UserRegisterDTO dto);

    User login(UserLoginDTO dto);

    User getUserById(String userId);

    List<User> getAllUsers();

    Page<User> getAllUsers(Pageable pageable);

    User getUserWithAddresses(String userId);

    User addAddress(String userId, User.Address address);

    void deleteAddress(String userId, String addressId);

    User updateUser(String userId, UserUpdateDTO dto);
}
