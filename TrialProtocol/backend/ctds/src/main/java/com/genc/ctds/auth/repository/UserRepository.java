//package com.genc.ctds.auth.repository;
//
//import com.genc.ctds.auth.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByUsername(String username);
//
//    boolean existsByUsername(String username);
//}
//
