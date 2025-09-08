package com.parser.core.common.repo;

import com.parser.core.common.dict.BaseDict;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseDictRepo<E extends BaseDict> {

    E save(E e);

    Optional<E> findByIdAndRdtIsNull(UUID id);

    List<E> findAllByRdtIsNull();

    Optional<E> getByIdAndRdtIsNull(UUID id);
}
