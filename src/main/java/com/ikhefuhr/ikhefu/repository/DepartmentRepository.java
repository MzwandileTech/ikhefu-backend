package com.ikhefuhr.ikhefu.repository;

import com.ikhefuhr.ikhefu.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);
}