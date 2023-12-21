package com.apps.mobileappws.io.repository;

import com.apps.mobileappws.io.entity.AddressEntity;
import com.apps.mobileappws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUserDetails(UserEntity userDetails);
    AddressEntity findByAddressId(String addressId);
}
