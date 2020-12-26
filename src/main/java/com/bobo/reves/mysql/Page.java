package com.bobo.reves.mysql;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * page 分页参数对象
 *
 * @author BO
 * @date 2020-12-26 13:31
 * @since 2020/12/26
 **/
@Data
@Accessors(chain = true)
public class Page {
    public Page() {
    }

    public Page(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    /**
     * 页码
     */
    private Integer page;
    /**
     * 行数
     */
    private Integer pageSize;

    public Integer calcPage() {
        if (page <= 0) {
            return 0;
        }
        return pageSize * (page - 1);
    }
}
