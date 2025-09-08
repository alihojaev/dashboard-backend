package com.parser.core.client.repo;

import com.parser.core.client.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepo extends JpaRepository<ClientEntity, UUID> {
    
    Optional<ClientEntity> findByEmail(String email);
    
    Optional<ClientEntity> findByUsername(String username);
    
    Optional<ClientEntity> findByGoogleId(String googleId);
    

    
    Optional<ClientEntity> findByPhone(String phone);
    
    @Query("SELECT c FROM ClientEntity c WHERE c.email = :email OR c.googleEmail = :email")
    Optional<ClientEntity> findByAnyEmail(@Param("email") String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByGoogleId(String googleId);
    

    
    boolean existsByPhone(String phone);
} 