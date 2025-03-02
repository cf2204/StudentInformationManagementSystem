package com.example.studentinformationmanagementsystem.dao;

import android.database.Cursor;

import com.example.studentinformationmanagementsystem.entity.Course;

import java.util.List;

public interface BaseDAO<T> {
    /**
     * 插入数据
     * @param entity 对应实体对象
     * @return 插入位置
     */
    long insert(T entity);

    /**
     * 更新实体对象
     * @param entity 对应实体对象
     * @return 更新的行数
     */
    int update(T entity);

    /**
     * 删除
     * @param id 删除的数据的id
     * @return 删除成功的行数
     */
    int delete(long id);

    /**
     * 根据id获得对象
     * @param id 对象的id
     * @return 实体对象
     */
    T findById(long id);

    /**
     * 根据名字获得id
     * @param name 对象的名字
     * @return 在表中的位置
     */
    long getIdByName(String name);

    /**
     * 获得所有对象
     * @return 对象列表
     */
    List<T> findAll();

    /**
     * 内部使用，将游标对象转化为列表
     * @param cursor 游标对象
     * @return 对象列表
     */
    List<T> ChangeToList(Cursor cursor);
}
