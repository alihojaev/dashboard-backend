package com.parser.core.auth.screen.service;

import com.parser.core.auth.role.enums.ScreenType;
import com.parser.core.auth.screen.entity.Screen;

import java.util.List;
import java.util.Optional;

public interface ScreenService {

    List<Screen> listAll();

    Optional<Screen> findByName(ScreenType name);
}
