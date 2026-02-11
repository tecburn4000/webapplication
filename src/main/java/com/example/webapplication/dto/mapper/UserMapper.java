package com.example.webapplication.dto.mapper;

import com.example.webapplication.dto.UserRegistrationDto;
import com.example.webapplication.dto.UserUpdateDto;
import com.example.webapplication.entities.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper (componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UserMapper {

    /**
     * Creates a new {@link UserUpdateDto} from an existing {@link User}
     * @param user - existing user data
     * @return - new {@link UserUpdateDto}
     */
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

}
