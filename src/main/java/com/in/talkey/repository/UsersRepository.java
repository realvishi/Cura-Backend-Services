package com.in.talkey.repository;

import com.in.talkey.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UsersRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmail(String email);

    @Query("SELECT u.confirmationToken FROM Users u WHERE u.email = ?1")
    Optional<String> findConfirmationTokenByEmail(String email);

    Optional<Integer> findIdByEmail(String email);

}
