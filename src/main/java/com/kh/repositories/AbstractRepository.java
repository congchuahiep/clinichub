package com.kh.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

/**
 * AbstractRepository là lớp trừu tượng cung cấp phương thức tiện ích để lấy Hibernate Session hiện tại.
 * Các repository khác <b>nên kế thừa</b> lớp này để tái sử dụng logic lấy Session, giúp code ngắn gọn và nhất quán.
 * 
 * <p>
 * 
 * Sử dụng:
 * <ul>
 *   <li> Kế thừa AbstractRepository trong các repository. </li>
 *   <li> Trong lớp con, tạo một constructor để inject bean LocalSessionFactoryBean </li>
 *   <li> Gọi getCurrentSession() để lấy Session làm việc với Hibernate. </li>
 * </ul>
 */
public abstract class AbstractRepository {
    @Autowired
    protected LocalSessionFactoryBean factory;

    protected Session getCurrentSession() {
        SessionFactory sessionFactory = this.factory.getObject();
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory is not initialized");
        }
        return sessionFactory.getCurrentSession();
    }
}
