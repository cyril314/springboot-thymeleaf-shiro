package com.fit.service;

import com.fit.dao.SysResourcesDao;
import com.fit.model.SysMenus;
import com.fit.model.SysResources;
import com.fit.common.base.BaseCrudService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2018/10/24
 */
@Service
public class SysResourcesService extends BaseCrudService<SysResourcesDao, SysResources> {

    public List<SysMenus> getMenusAll() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT `id`,`name`,`url`,`pid` FROM `sys_resources` WHERE `enabled`=1");
        return this.getJdbcTemplate().query(sb.toString(), new RowMapper<SysMenus>() {
            @Override
            public SysMenus mapRow(ResultSet rs, int i) throws SQLException {
                SysMenus menu = new SysMenus();
                menu.setId(rs.getLong("id"));
                menu.setName(rs.getString("name"));
                menu.setUrl(rs.getString("url"));
                menu.setPid(rs.getInt("pid"));
                return menu;
            }
        });
    }

}
