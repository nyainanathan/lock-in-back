package com.nathan.lock_in.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<Users> {

    @Override
    public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Users(
                rs.getString("user_id"),
                rs.getString("user_first_name"),
                rs.getString("user_last_name"),
                rs.getString("user_email"),
                rs.getTimestamp("user_created_at").toInstant(),
                rs.getString("user_password_hash")
        );
    }
}
