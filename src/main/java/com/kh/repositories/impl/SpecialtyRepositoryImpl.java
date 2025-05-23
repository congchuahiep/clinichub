package com.kh.repositories.impl;

import com.kh.dtos.SpecialtyDTO;
import com.kh.pojo.Specialty;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.SpecialtyRepository;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class SpecialtyRepositoryImpl extends AbstractRepository<Specialty, Long> implements SpecialtyRepository {

    public SpecialtyRepositoryImpl() {
        super(Specialty.class);
    }

    @Override
    public List<SpecialtyDTO> findAll(){
        // Mở session hiện tại
        Session session = getCurrentSession();

        // Truy vấn HQL để lấy tất cả chuyên khoa dưới dạng DTO
        String hql = "SELECT new com.kh.dtos.SpecialtyDTO(s.id, s.name) FROM Specialty s";

        // Thực thi truy vấn và trả về danh sách kết quả
        return session.createQuery(hql, SpecialtyDTO.class).getResultList();
    }

}
