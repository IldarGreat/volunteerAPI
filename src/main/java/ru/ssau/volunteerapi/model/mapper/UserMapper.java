package ru.ssau.volunteerapi.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.ssau.volunteerapi.model.dto.patch.UserPatch;
import ru.ssau.volunteerapi.model.dto.request.UserRequest;
import ru.ssau.volunteerapi.model.dto.response.UserResponse;
import ru.ssau.volunteerapi.model.entitie.User;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toEntity(UserRequest userRequest);

    User toEntity(UserResponse userResponse);

    User toEntity(UserPatch userPatch, @MappingTarget User user);



    UserRequest toRequest(User user);

    UserResponse toResponse(User user);

}