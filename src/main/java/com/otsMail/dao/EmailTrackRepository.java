package com.otsMail.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.otsMail.model.EmailTrack;

@Repository
public interface EmailTrackRepository extends JpaRepository<EmailTrack, String> {

}
