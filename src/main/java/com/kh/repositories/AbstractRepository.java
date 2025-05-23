package com.kh.repositories;

import com.kh.utils.PaginatedResult;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AbstractRepository là lớp trừu tượng cung cấp phương thức tiện ích để lấy Hibernate Session hiện tại.
 * Các repository khác <b>nên kế thừa</b> lớp này để tái sử dụng logic lấy Session, giúp code ngắn gọn và nhất quán.
 *
 * <p>
 * Ngoài ra, chúng thực hiện sẵn các phương thức có sẵn phục vụ cho CURD
 * </p>
 * Sử dụng:
 * <ul>
 *   <li> Kế thừa AbstractRepository trong các repository. </li>
 *   <li> Trong lớp con, tạo một constructor để inject bean LocalSessionFactoryBean </li>
 *   <li> Gọi getCurrentSession() để lấy Session làm việc với Hibernate. </li>
 * </ul>
 */
@Transactional
public abstract class AbstractRepository<T, ID> implements GenericRepository<T, ID> {

    @Autowired
    protected LocalSessionFactoryBean factory;

    private final Class<T> entityClass;


    protected AbstractRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected Session getCurrentSession() throws IllegalStateException {
        SessionFactory sessionFactory = this.factory.getObject();
        if (sessionFactory == null) {
            throw new IllegalStateException("Không thể thực thi truy vấn! SessionFactory chưa được khởi tạo");
        }
        return sessionFactory.getCurrentSession();
    }

    /**
     * Lưu thực thể pojo vào cơ sở dữ liệu
     */
    @Override
    public T save(T entity) {
        getCurrentSession().persist(entity);
        return entity;
    }

    /**
     * Cập nhật thực thể pojo vào cơ sở dữ liệu
     */
    @Override
    public T update(T entity) {
        return getCurrentSession().merge(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(getCurrentSession().get(entityClass, id));
    }

    @Override
    public void delete(T entity) {
        getCurrentSession().remove(entity);
    }

    @Override
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Phương thức lấy danh sách
     */
    @Override
    public List<T> list() {
        Session session = getCurrentSession();
        String hql = "FROM " + entityClass.getName();

        Query<T> query = session.createQuery(hql, entityClass);

        return query.getResultList();
    }

    /**
     * Phương thức lấy danh sách có tham số phân trang, sắp xếp
     *
     * @param params Tham số thêm (có thể null): phân trang, sắp xếp
     */
    @Override
    public PaginatedResult<T> paginatedList(Map<String, String> params) {
        Session session = getCurrentSession();
        String selectHql = "FROM " + entityClass.getName();
        String countHql = "SELECT COUNT(t) FROM " + entityClass.getName() + " t";

        Query<T> selectQuery = session.createQuery(selectHql, entityClass);
        Query<Long> countQuery = session.createQuery(countHql, Long.class);

        int page = 1;
        int pageSize = 10;
        if (params != null && !params.containsKey("page")) {
            page = Integer.parseInt(params.getOrDefault("page", "1"));
            pageSize = Integer.parseInt(params.getOrDefault("pageSize", "10"));
            selectQuery.setFirstResult((page - 1) * pageSize);
            selectQuery.setMaxResults(pageSize);
        }

        List<T> elements = selectQuery.getResultList();
        Long totalElements = countQuery.getSingleResult();

        return new PaginatedResult<>(elements, page, pageSize, totalElements);
    }
}

