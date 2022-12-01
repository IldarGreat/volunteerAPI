package ru.ssau.volunteerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssau.volunteerapi.model.entitie.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
