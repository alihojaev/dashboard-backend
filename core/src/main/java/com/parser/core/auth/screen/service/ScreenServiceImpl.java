package com.parser.core.auth.screen.service;

import com.parser.core.auth.role.enums.ScreenType;
import com.parser.core.auth.screen.entity.Screen;
import com.parser.core.auth.screen.jdbc.ScreenDao;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ScreenServiceImpl implements ScreenService {

    ScreenDao dao;

    @Override
    public List<Screen> listAll() {
        return dao.listAll();
    }

    @Override
    public Optional<Screen> findByName(ScreenType name) {
        return dao.findByName(name);
    }

    public Map<ScreenType, Screen> initScreens() {
        var screens = listAll();
        var screenSet = screens.stream()
                .peek(s -> s.setDescription(s.getName().getDescription()))
                .map(Screen::getName)
                .collect(Collectors.toSet());

        for (var s : ScreenType.values()) {
            if (!screenSet.contains(s)) {
                screens.add(new Screen(
                        null,
                        s,
                        s.getDescription(),
                        new Timestamp(System.currentTimeMillis())
                ));
            }
        }

        var newScreens = dao.saveAll(screens);

        if (newScreens.size() != screens.size()) {
            throw new RuntimeException();
        }

        return newScreens.stream().collect(Collectors.toMap(
                Screen::getName,
                s -> s
        ));
    }
}
