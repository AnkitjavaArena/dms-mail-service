package com.otsMail.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otsMail.model.Enroll;

@Repository
public interface EnrollRepository extends JpaRepository<Enroll, Long> {
	//TODO use Optional return type to handle better way
	List<Enroll> findByToIn(List<String> to);
	Optional<Enroll> findByTo(String to);
	List<Enroll> findByStatusIgnoreCaseAndSubscribeTrue(String status);
	
}
