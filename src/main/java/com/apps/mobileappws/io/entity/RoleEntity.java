package com.apps.mobileappws.io.entity;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "roles")
public class RoleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -6210048194103496376L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<UserEntity> users;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_authorities",
            joinColumns = @JoinColumn(name = "roles_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name= "authorities_id",  referencedColumnName = "id"))
    private Collection<AuthorityEntity> authorities;

    public RoleEntity() {

    }

    public RoleEntity(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Collection<UserEntity> users) {
        this.users = users;
    }

    public Collection<AuthorityEntity> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<AuthorityEntity> authorities) {
        this.authorities = authorities;
    }

}