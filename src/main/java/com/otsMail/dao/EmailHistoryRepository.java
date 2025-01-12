package com.otsMail.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otsMail.model.EmailHistory;

@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistory, Long> {

}
