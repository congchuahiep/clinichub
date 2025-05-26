/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.repositories.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.kh.dtos.DoctorWithRating;
import com.kh.enums.UserRole;
import com.kh.pojo.DoctorLicense;
import com.kh.pojo.Hospital;
import com.kh.utils.PaginatedResult;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
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
 * @author congchuahiep
 */
@Repository
@Transactional
public class UserRepositoryImpl extends AbstractRepository<User, Long> implements UserRepository {

    public UserRepositoryImpl() {
        super(User.class);
    }

    /**
     * Thêm đối tượng User dùng vào cơ sở dữ liệu
     *
     * @param user Đối tượng user cần thêm
     * @return Đối tượng user mới được tạo và lưu dưới cơ sở dữ liệu
     */
    @Override
    public User save(User user) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, IllegalStateException {
        try {
            return super.save(user);
        } catch (ConstraintViolationException ex) {
            String message = ex.getMessage();
            if (message.contains("users.username")) {
                throw new UsernameAlreadyExistsException("Username này đã có người khác sử dụng!");
            } else if (message.contains("users.email")) {
                throw new EmailAlreadyExistsException("Email này đã có người khác sử dụng!");
            } else if (message.contains("license_number")) {
                throw new EmailAlreadyExistsException("Giấy phép hành nghề không hợp lệ, trong hệ thống đã có người sử dụng!");
            }
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public PaginatedResult<DoctorWithRating> doctorList(Map<String, String> params) {
        Session session = getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // Query để lấy danh sách bác sĩ
        CriteriaQuery<User> doctorCriteriaQuery = builder.createQuery(User.class);
        Root<User> root = doctorCriteriaQuery.from(User.class);


        // Join bảng khác để đồng thời trả về dữ liệu bằng hành nghề và các bệnh viện mà bác sĩ làm
        Fetch<User, DoctorLicense> doctorLicenseFetch = root.fetch("doctorLicenseSet", JoinType.LEFT);
        Fetch<User, Hospital> hospitalFetch = root.fetch("hospitalSet", JoinType.LEFT);
        doctorLicenseFetch.fetch("specialtyId", JoinType.LEFT);

        // Xử lý vị ngữ (các điều kiện truy vấn)
        List<Predicate> predicates = createDoctorPredicates(builder, root, params);
        doctorCriteriaQuery.where(predicates.toArray(new Predicate[0])); // Đổi các vị ngữ thành điều kiện truy vấn)
        doctorCriteriaQuery.distinct(true); // Distinct để tránh duplicate records

        // Xử lý phân trang
        int page = 1;
        int pageSize = 5;
        if (!params.isEmpty() && params.containsKey("page")) {
            page = Integer.parseInt(params.getOrDefault("page", "1"));
            pageSize = Integer.parseInt(params.getOrDefault("pageSize", "5"));
        }

        List<User> doctors = session.createQuery(doctorCriteriaQuery)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        // Tính toán average rating cho mỗi bác sĩ
        List<DoctorWithRating> doctorsWithRating = doctors.stream()
                .map(doctor -> {
                    Double avgRating = session.createQuery(
                                    "SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.doctorId = :doctor",
                                    Double.class)
                            .setParameter("doctor", doctor)
                            .getSingleResult();
                    return new DoctorWithRating(doctor, avgRating);
                })
                .collect(Collectors.toList());


        Long count = this.countDoctor(params);

        return new PaginatedResult<>(
                doctorsWithRating, page, pageSize, count
        );
    }

    @Override
    public PaginatedResult<User> doctorListWithoutRating(Map<String, String> params) {
        Session session = getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // Tạo CriteriaQuery để lấy danh sách bác sĩ
        CriteriaQuery<User> doctorCriteriaQuery = builder.createQuery(User.class);
        Root<User> root = doctorCriteriaQuery.from(User.class);

        // Join bảng khác để đồng thời trả về dữ liệu bằng hành nghề và các bệnh viện mà bác sĩ làm
        Fetch<User, DoctorLicense> doctorLicenseFetch = root.fetch("doctorLicenseSet", JoinType.LEFT);
        Fetch<User, Hospital> hospitalFetch = root.fetch("hospitalSet", JoinType.LEFT);
        doctorLicenseFetch.fetch("specialtyId", JoinType.LEFT);

        // Thêm các điều kiện truy vấn
        List<Predicate> predicates = createDoctorPredicates(builder, root, params);
        doctorCriteriaQuery.where(predicates.toArray(new Predicate[0]));

        // Thêm tùy chọn phân trang
        int page = params.containsKey("page") ? Integer.parseInt(params.getOrDefault("page", "1")) : 1;
        int pageSize = params.containsKey("pageSize") ? Integer.parseInt(params.getOrDefault("pageSize", "10")) : 10;

        Query<User> query = session.createQuery(doctorCriteriaQuery)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize);

        // Lấy danh sách bác sĩ
        List<User> doctors = query.getResultList();

        // Tính tổng số bác sĩ phù hợp
        Long totalDoctors = countDoctor(params);

        // Trả về kết quả phân trang
        return new PaginatedResult<>(doctors, page, pageSize, totalDoctors);
    }

    @Override
    public Optional<User> findDoctorById(Long id) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery(
                "FROM User WHERE id = :id AND role = :role AND isActive = true",
                User.class
        );

        query.setParameter("id", id);
        query.setParameter("role", UserRole.DOCTOR);

        return Optional.ofNullable(query.getSingleResult());
    }

    @Override
    public Optional<User> findDoctorByIdWithLicense(Long id) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery(
                "SELECT DISTINCT u FROM User u " +
                        "JOIN FETCH u.doctorLicenseSet dl " +
                        "WHERE u.id = :id " +
                        "AND u.role = :role " +
                        "AND u.isActive = false ",
                User.class
        );
        query.setParameter("id", id);
        query.setParameter("role", UserRole.DOCTOR);
        return query.uniqueResultOptional();
    }




    @Override
    public Optional<DoctorWithRating> findDoctorProfileById(Long id) {
        Session session = getCurrentSession();
        String hql = "SELECT u FROM User u" +
                " LEFT JOIN FETCH u.doctorLicenseSet dl" +
                " LEFT JOIN FETCH u.hospitalSet" +
                " LEFT JOIN FETCH dl.specialtyId" +
                " WHERE u.id = :id AND u.role = :role AND u.isActive = true";

        Query<User> query = session.createQuery(hql, User.class);

        query.setParameter("id", id);
        query.setParameter("role", UserRole.DOCTOR);

        User doctor = query.getSingleResult();

        Double avgRating = session.createQuery(
                        "SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.doctorId = :doctor",
                        Double.class)
                .setParameter("doctor", doctor)
                .getSingleResult();

        return Optional.of(new DoctorWithRating(doctor, avgRating));

    }

    /**
     * Lấy đối tượng User từ cơ sở dữ liệu bằng username
     *
     * @param username - Tên người dùng
     * @return Người dùng khớp với tên người dùng
     * @throws UsernameNotFoundException Nếu như người dùng không tồn tại
     */
    @Override
    public Optional<User> findByUsername(String username) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery(
                "FROM User WHERE username = :username AND isActive = true",
                User.class
        );
        query.setParameter("username", username);

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
    public Optional<User> findDoctorByUsername(String username) {
        Session session = getCurrentSession();
        Query<User> query = session.createQuery(
                "FROM User WHERE username = :username AND role = :role AND isActive = true",
                User.class
        );
        query.setParameter("username", username);
        query.setParameter("role", UserRole.DOCTOR);

        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Long countDoctor(Map<String, String> params) {
        Session session = getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<User> root = criteria.from(User.class);

        // Select count
        criteria.select(builder.countDistinct(root));

        // Add predicates
        List<Predicate> predicates = createDoctorPredicates(builder, root, params);
        criteria.where(predicates.toArray(new Predicate[0]));

        return session.createQuery(criteria).getSingleResult();
    }


    /**
     * Tạo vị ngữ (các điều kiện lặp) để bổ trợ cho việc tìm kiếm bác sĩ
     * <p>
     * Được sử dụng cho {@code doctorList} và {@code countDoctor}
     * </P>
     */
    private List<Predicate> createDoctorPredicates(
            CriteriaBuilder builder,
            Root<User> root,
            Map<String, String> params
    ) {

        // Tạo danh sách các vị ngữ (tức mấy câu điều kiện lọc)
        List<Predicate> predicates = new ArrayList<>();

        // Điều kiện cơ bản
        predicates.add(builder.equal(root.get("role"), UserRole.DOCTOR));

        if (params.containsKey("isFalse")) {
            predicates.add(builder.isFalse(root.get("isActive")));
        } else {
            predicates.add(builder.isTrue(root.get("isActive")));
        }

        // Tìm kiếm theo bệnh viên
        Long hospitalId = params.containsKey("hospitalId") ? Long.parseLong(params.get("hospitalId")) : null;
        if (hospitalId != null) {
            Join<User, Hospital> hospitalJoin = root.join("hospitalSet", JoinType.INNER);
            predicates.add(builder.equal(hospitalJoin.get("id"), hospitalId));
        }

        // Tìm kiếm theo chuyên khoa
        Long specialtyId = params.containsKey("specialtyId") ? Long.parseLong(params.get("specialtyId")) : null;
        if (specialtyId != null) {
            Join<User, DoctorLicense> licenseJoin = root.join("doctorLicenseSet", JoinType.INNER);
            predicates.add(builder.equal(licenseJoin.get("specialtyId").get("id"), specialtyId));
        }

        // Tìm kiếm theo tên bác sĩ
        String doctorName = params.getOrDefault("doctorName", null);
        if (doctorName != null && !doctorName.trim().isEmpty()) {
            String pattern = "%" + doctorName.toLowerCase() + "%";
            Predicate firstNameMatch = builder.like(builder.lower(root.get("firstName")), pattern);
            Predicate lastNameMatch = builder.like(builder.lower(root.get("lastName")), pattern);
            predicates.add(builder.or(firstNameMatch, lastNameMatch));
        }

        return predicates;
    }

}
