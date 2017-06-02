package com.guitar.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guitar.db.model.ModelType;

public interface ModelTypeJpaRepository extends JpaRepository<ModelType, Long> {

	List<ModelType> findByNameIsNull();
	
	List<ModelType> findByNameIsNotNull();
	
	List<ModelType> findByNameNotNull();
}
