package com.example.webapplication.dto.mapper;

import com.example.webapplication.dto.UserRegistrationDto;
import com.example.webapplication.dto.UserUpdateDto;
import com.example.webapplication.entities.Authority;
import com.example.webapplication.entities.User;
import com.example.webapplication.repositories.security.AuthorityRepository;
import org.mapstruct.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mapper (componentModel = "spring")
public interface UserMapper {

    /**
     * Creates a new {@link UserUpdateDto} from an existing {@link User}
     * @param user - existing user data
     * @return - new {@link UserUpdateDto}
     */
    @Mapping(source = "authorities", target = "role")
    UserUpdateDto toUserUpdateDTO(User user);

    /**
     * Updates an existing {@link User} from an {@link UserUpdateDto}
     * @param dto - new user data
     * @param user - existing user to be updated
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    void updateUserFromUserUpdateDto(UserUpdateDto dto, @MappingTarget User user);

    /**
     * Creates a new {@link UserRegistrationDto} from an existing {@link User}
     * @param user - existing user data
     * @return new {@link UserUpdateDto}
     */
    @Mapping(target = "matchingPassword", ignore = true)
    UserRegistrationDto toUserRegistrationDTO(User user);

    /**
     * Creates a new {@link User} from an {@link UserRegistrationDto}
     * @param userRegistrationDto - given user data
     * @return new {@link User}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    User toUserEntity(UserRegistrationDto userRegistrationDto);

    /**
     * Maps the first entry of authorities to a role String
     * @param authorities - a set of authorities
     * @return role
     */
    default String mapAuthoritiesToRole(Set<Authority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return null;
        }

        // use first role
        return authorities.stream()
                .map(Authority::getRole)
                .findFirst()
                .orElse(null);
    }
}
