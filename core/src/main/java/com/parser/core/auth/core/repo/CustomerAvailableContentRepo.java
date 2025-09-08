package com.parser.core.auth.core.repo;

import com.parser.core.auth.core.entity.CustomerAvailableContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerAvailableContentRepo extends JpaRepository<CustomerAvailableContent, UUID> {

}
