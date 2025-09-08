package com.parser.core.common.service;

import com.parser.core.common.dict.BaseDict;
import com.parser.core.common.dto.BaseDictDto;
import com.parser.core.common.repo.BaseDictRepo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class BaseDictServiceImpl<E extends BaseDict, M extends BaseDictDto, R extends BaseDictRepo<E>> implements BaseDictService<E, M> {

    protected R repo;

    protected static <T extends BaseDict> T fromModelBase(T entity, BaseDictDto model) {

        if (entity.getId() != null) entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());

        return entity;
    }

    protected static <TM extends BaseDictDto, TE extends BaseDict> TM toModelBase(TM model, TE entity) {

        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());

        return model;
    }

    protected BaseDictRepo<E> baseRepo() {
        return repo;
    }

    @Override
    public UUID save(M model) {
        model.validate();

        return repo.save(fromModel(model)).getId();
    }

    @Override
    public Optional<E> findByIdAndRdtIsNull(UUID dictId) {
        return repo.findByIdAndRdtIsNull(dictId);
    }

    @Override
    public List<M> listAllAsModel() {
        return repo.findAllByRdtIsNull().stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public void delete(UUID dictId) {
        repo.getByIdAndRdtIsNull(dictId)
                .ifPresent(e -> {
                    e.markRemoved();
                    repo.save(e);
                });
    }
}
