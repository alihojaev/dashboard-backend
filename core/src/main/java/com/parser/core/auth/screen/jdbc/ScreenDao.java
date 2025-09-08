package com.parser.core.auth.screen.jdbc;


import com.parser.core.auth.role.enums.ScreenType;
import com.parser.core.auth.screen.entity.Screen;
import com.parser.core.common.jdbc.EntityDao;

import java.util.List;
import java.util.Optional;

public interface ScreenDao extends EntityDao<Screen> {

    List<Screen> listAll();

    Optional<Screen> findByName(ScreenType name);
}
