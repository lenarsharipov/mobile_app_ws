package com.apps.mobileappws.io.repository;

import com.apps.mobileappws.io.entity.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {
    AuthorityEntity findByName(String name);
}
