package by.bogdan.criminalintent.dao;

import java.util.List;
import java.util.UUID;

public interface Crud<T> {
    void insert(T entity);
    void update(T entity);
    List<T> getAll();
    T getByUuid(UUID uuid);
    boolean delete(UUID uuid);
}
