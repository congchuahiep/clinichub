/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kh.repositories;

import com.kh.exceptions.EmailAlreadyExistsException;
import com.kh.exceptions.UsernameAlreadyExistsException;
import com.kh.pojo.User;

import java.util.List;

/**
 *
 */
public interface UserRepository {
    List<User> getUserList();

    User getUserById(long id);

    User getUserByUsername(String username);

    User addUser(User user) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, IllegalStateException;
}
