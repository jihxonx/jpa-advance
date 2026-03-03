package org.example.jpaadvance.repository;

import org.example.jpaadvance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
