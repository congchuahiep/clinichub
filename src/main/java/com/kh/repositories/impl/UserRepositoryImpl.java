/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.repositories.impl;

import com.kh.pojo.User;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.UserRepository;
import org.hibernate.query.Query;
import java.util.List;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author congchuahiep
 */
@Repository
@Transactional
public class UserRepositoryImpl extends AbstractRepository implements UserRepository {

    private BCryptPasswordEncoder passwordEncoder;

    public UserRepositoryImpl(LocalSessionFactoryBean factory, BCryptPasswordEncoder passwordEncoder) {
        this.factory = factory;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public List<User> getUser() {
        Session session = getCurrentSession();

        Query<User> q = session.createQuery("FROM User", User.class);
        return q.getResultList();
    }

    @Override
    public User getUserByUsername(String username) {
        Session session = getCurrentSession();
        Query<User> query = session.createNamedQuery("User.findByUsername", User.class);
        query.setParameter("username", username);

        return (User) query.getSingleResult();
    }

    @Override
    public User addUser(User user) {
        Session session = getCurrentSession();
        session.persist(user);

        return user;
    }

    @Override
    public boolean authenticate(String username, String password) {
        User u = this.getUserByUsername(username);

        return this.passwordEncoder.matches(password, u.getPassword());
    }
}
