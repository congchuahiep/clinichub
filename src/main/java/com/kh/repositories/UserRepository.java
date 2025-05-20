/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kh.repositories;

import com.kh.exceptions.EmailAlreadyExistsException;
import com.kh.exceptions.UsernameAlreadyExistsException;
import com.kh.pojo.User;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public interface UserRepository {
    List<User> getUserList();

    Optional<User> getUserById(long id);

    Optional<User> getDoctorById(long id);

    Optional<User> getUserByUsername(String username);

    User addUser(User user) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, IllegalStateException;
    
    Optional<User> findById(Long id);

}
