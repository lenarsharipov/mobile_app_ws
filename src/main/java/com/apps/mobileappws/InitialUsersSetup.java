package com.apps.mobileappws;

import com.apps.mobileappws.io.entity.AuthorityEntity;
import com.apps.mobileappws.io.entity.RoleEntity;
import com.apps.mobileappws.io.entity.UserEntity;
import com.apps.mobileappws.io.repository.AuthorityRepository;
import com.apps.mobileappws.io.repository.RoleRepository;
import com.apps.mobileappws.io.repository.UserRepository;
import com.apps.mobileappws.shared.Utils;
import jakarta.transaction.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class InitialUsersSetup {
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final Utils utils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public InitialUsersSetup(AuthorityRepository authorityRepository,
                             RoleRepository roleRepository,
                             UserRepository userRepository,
                             Utils utils,
                             BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("From Application ready event... ");
        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        RoleEntity roleUser = createRole(
                "ROLE_USER",
                List.of(readAuthority, writeAuthority));
        RoleEntity roleAdmin = createRole(
                "ROLE_ADMIN",
                List.of(readAuthority, writeAuthority, deleteAuthority));

        if (roleAdmin == null) return;

        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName("Admin");
        adminUser.setLastName("Admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setEmailVerificationStatus(true);
        adminUser.setUserId(utils.generateUserId(30));
        adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("123"));
        adminUser.setRoles(List.of(roleAdmin));

        userRepository.save(adminUser);
    }

    @Transactional
    protected AuthorityEntity createAuthority(String name) {
        AuthorityEntity authority = authorityRepository.findByName(name);
        if (authority == null) {
            authority = new AuthorityEntity(name);
            authorityRepository.save(authority);
        }
        return authority;
    }

    @Transactional
    protected RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }
        return role;
    }

}