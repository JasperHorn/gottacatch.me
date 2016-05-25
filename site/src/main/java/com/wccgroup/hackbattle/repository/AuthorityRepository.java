package com.wccgroup.hackbattle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wccgroup.hackbattle.domain.Authority;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
