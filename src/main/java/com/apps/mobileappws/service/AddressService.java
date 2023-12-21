package com.apps.mobileappws.service;

import com.apps.mobileappws.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {

    List<AddressDto> getAddresses(String userId);

    AddressDto getAddress(String addressId);

}
