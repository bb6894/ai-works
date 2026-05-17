package com.example.smartagri.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.smartagri.common.BizException;
import com.example.smartagri.dto.PageResult;
import java.util.List;

public abstract class CrudService<T> {
    protected abstract BaseMapper<T> mapper();

    protected LambdaQueryWrapper<T> query(String keyword) {
        return new LambdaQueryWrapper<>();
    }

    public PageResult<T> page(int page, int size, String keyword) {
        Page<T> result = mapper().selectPage(Page.of(page, size), query(keyword));
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public List<T> listAll() {
        return mapper().selectList(new LambdaQueryWrapper<>());
    }

    public T get(Long id) {
        T value = mapper().selectById(id);
        if (value == null) {
            throw new BizException("记录不存在: " + id);
        }
        return value;
    }

    public T create(T entity) {
        mapper().insert(entity);
        return entity;
    }

    public T update(Long id, T entity) {
        setId(entity, id);
        mapper().updateById(entity);
        return get(id);
    }

    public void delete(Long id) {
        mapper().deleteById(id);
    }

    protected abstract void setId(T entity, Long id);
}
