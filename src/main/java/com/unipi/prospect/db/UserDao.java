package com.unipi.prospect.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface UserDao<T> {
    public boolean insert(T t);
    public boolean update(T t);
    public boolean delete(T t);
    public ArrayList<T> selectAll() throws SQLException;
    public T selectByUsername(String username) throws SQLException;
}
