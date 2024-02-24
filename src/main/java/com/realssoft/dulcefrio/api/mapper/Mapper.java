package com.realssoft.dulcefrio.api.mapper;

public interface Mapper<E, R>
{

    E toEntity(R dto);
    R toDto(E entity);

}
