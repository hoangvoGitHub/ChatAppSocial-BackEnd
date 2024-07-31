package com.hoangvo.chatappsocial.auth.data.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_user")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Nonnull
    @Column(columnDefinition="TEXT")
    private String username;

    @JsonIgnore
    @Nonnull
    private String password;

    @Nonnull
    @Column(name = "first_name", columnDefinition="TEXT")
    private String firstName;

    @Nonnull
    @Column(name = "last_name",columnDefinition="TEXT")
    private String lastName;

    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    @Nonnull
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Date updatedAt;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Date deletedAt;

    @Enumerated(EnumType.STRING)
    @Nonnull
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
