package com.parser.core.common.service;


import com.parser.core.common.dict.BaseDict;
import com.parser.core.common.dto.BaseDictDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseDictService<E extends BaseDict, M extends BaseDictDto> {

    UUID save(M model);

    Optional<E> findByIdAndRdtIsNull(UUID dictId);

    List<M> listAllAsModel();

    void delete(UUID dictId);

    E fromModel(M model);

    M toModel(E entity);
}
