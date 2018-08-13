package by.bogdan.criminalintent.dao;

public interface Crud<T> {
    void insert(T entity);
    void update(T entity);
}
