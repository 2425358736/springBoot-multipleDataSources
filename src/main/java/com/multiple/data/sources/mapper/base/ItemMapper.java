package com.multiple.data.sources.mapper.base;

import com.multiple.data.sources.domain.base.Item;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author admin
 */
@Mapper
public interface ItemMapper {
    /**
     * 插入
     * @param item
     * @return
     */
    int insertSelective(Item item);
}
