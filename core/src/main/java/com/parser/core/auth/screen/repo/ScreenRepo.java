package com.parser.core.auth.screen.repo;

import com.parser.core.auth.role.enums.ScreenType;
import com.parser.core.auth.screen.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScreenRepo extends JpaRepository<Screen, Long> {

    Optional<Screen> findAllByName(ScreenType name);
}
