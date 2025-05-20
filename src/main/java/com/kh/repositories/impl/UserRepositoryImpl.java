/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.repositories.impl;

import java.util.List;

import com.kh.enums.UserRole;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kh.exceptions.EmailAlreadyExistsException;
import com.kh.exceptions.UsernameAlreadyExistsException;
import com.kh.pojo.User;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.UserRepository;
import jakarta.persistence.EntityManager;

import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;

/**
 * @author congchuahiep
 */
@Repository
@Transactional
public class UserRepositoryImpl extends AbstractRepository implements UserRepository {

    public UserRepositoryImpl(LocalSessionFactoryBean factory) {
        this.factory = factory;
    }
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> getUserList() {
        Session session = getCurrentSession();

        Query<User> q = session.createQuery("FROM User", User.class);
        return q.getResultList();
    }

    @Override
    public Optional<User> getUserById(long id) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery("FROM User WHERE id = :id", User.class);
        query.setParameter("id", id);

        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getDoctorById(long id) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery(
                "FROM User WHERE id = :id AND role = :role AND isActive = true",
                User.class
        );
        query.setParameter("id", id);
        query.setParameter("role", UserRole.DOCTOR);

        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Lấy đối tượng User từ cơ sở dữ liệu bằng username
     *
     * @param username - Tên người dùng
     * @return Người dùng khớp với tên người dùng
     * @throws UsernameNotFoundException Nếu như người dùng không tồn tại
     */
    @Override
    public Optional<User> getUserByUsername(String username) {
        Session session = getCurrentSession();
        Query<User> query = session.createNamedQuery("User.findByUsername", User.class);
        query.setParameter("username", username);

        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Thêm đối tượng User dùng vào cơ sở dữ liệu
     *
     * @param user Đối tượng user cần thêm
     * @return Đối tượng user mới được tạo và lưu dưới cơ sở dữ liệu
     */
    @Override
    public User addUser(User user) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, IllegalStateException {
        try {
            Session session = getCurrentSession();
            session.persist(user);
        } catch (ConstraintViolationException ex) {
            String message = ex.getMessage();

            if (message.contains("username")) {
                throw new UsernameAlreadyExistsException("Username này đã có người khác sử dụng!");
            } else if (message.contains("email")) {
                throw new EmailAlreadyExistsException("Email này đã có người khác sử dụng!");
            }
        }

        return user;
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }
}
