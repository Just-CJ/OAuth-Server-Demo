package com.example.dao;

import com.example.bean.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Just_CJ on 2015/12/14.
 */
@Repository
public class UserDAO {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public UserInfo getUserInfo(String username){
        String sql = "SELECT u.username name, u.password pass, a.authority role FROM "+
                "users u INNER JOIN authorities a on u.username=a.username WHERE "+
                "u.enabled = true and u.username = ?";

        UserInfo userInfo = (UserInfo)jdbcTemplate.queryForObject(sql, new Object[]{username},
                new RowMapper<UserInfo>() {
                    public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                        UserInfo user = new UserInfo();
                        user.setUsername(rs.getString("name"));
                        user.setPassword(rs.getString("pass"));
                        user.setRole(rs.getString("role"));
                        return user;
                    }
                });

        return userInfo;
    }

}
