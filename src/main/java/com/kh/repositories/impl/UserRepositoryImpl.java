/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.repositories.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kh.exceptions.EmailAlreadyExistsException;
import com.kh.exceptions.UsernameAlreadyExistsException;
import com.kh.pojo.User;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.UserRepository;

import jakarta.persistence.NoResultException;

/**
 *
 * @author congchuahiep
 */
@Repository
@Transactional
public class UserRepositoryImpl extends AbstractRepository implements UserRepository {

    public UserRepositoryImpl(LocalSessionFactoryBean factory) {
        this.factory = factory;
    }

    @Transactional
    @Override
    public List<User> getUser() {
        Session session = getCurrentSession();

        Query<User> q = session.createQuery("FROM User", User.class);
        return q.getResultList();
    }

    /**
     * Lấy đối tượng User từ cơ sở dữ liệu bằng username
     * 
     * @param username - Tên người dùng
     * @return Người dùng khớp với tên người dùng
     * @throws UsernameNotFoundException Nếu như người dùng không tồn tại
     */
    @Override
    public User getUserByUsername(String username) {
        Session session = getCurrentSession();
        Query<User> query = session.createNamedQuery("User.findByUsername", User.class);
        query.setParameter("username", username);

        try {
            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            throw new BadCredentialsException("Tài khoản hoặc mật khẩu không chính xác!");
        }
    }

    /**
     * Thêm đối tượng User dùng vào cơ sở dữ liệu
     * 
     * @param user - Đối tượng user cần thêm
     * @return Đối tượng user mới được tạo và lưu dưới cơ sở dữ liệu
     */
    @Override
    public User addUser(User user) {
        Session session = getCurrentSession();

        Query<User> usernameQuery = session.createQuery("FROM User WHERE username = :username", User.class);
        usernameQuery.setParameter("username", user.getUsername());
        if (!usernameQuery.getResultList().isEmpty()) {
            throw new UsernameAlreadyExistsException("Username này đã có người khác sử dụng!");
        }

        Query<User> emailQuery = session.createQuery("FROM User WHERE email = :email", User.class);
        emailQuery.setParameter("email", user.getEmail());
        if (!emailQuery.getResultList().isEmpty()) {
            throw new EmailAlreadyExistsException("Email này đã có người khác sử dụng!");
        }

        session.persist(user);

        return user;
    }
}
