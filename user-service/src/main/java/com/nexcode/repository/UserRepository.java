package com.nexcode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nexcode.models.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
