package com.bobo.reves.mysql;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 分页查询结果对象
 *
 * @author BO
 * @date 2020-12-26 13:31
 * @since 2020/12/26
 **/
@Data
@Accessors(chain = true)
public class Pagination<T> {
    /**
     * 查询结果
     */
    private List<T> data;
    /**
     * 分页对象
     */
    private Page page;
    /**
     * 数据总数
     */
    private Integer count;
}
