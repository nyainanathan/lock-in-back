package com.nathan.lock_in.chronos;

import com.nathan.lock_in.auth.MinimalUserInfo;
import com.nathan.lock_in.user.UserRowMapper;
import com.nathan.lock_in.user.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class ChronosRowMapper implements RowMapper<Chronos> {

    private final UserRowMapper userRowMapper;

    @Override
    public Chronos mapRow(ResultSet rs, int rowNum) throws SQLException {
        Chronos chrono = new Chronos();
        chrono.setId(rs.getString("chrono_id"));
        chrono.setUnit(DurationUnit.valueOf(rs.getString("chrono_unit")));
        chrono.setTitle(rs.getString("chrono_title"));
        chrono.setDuration(rs.getDouble("chrono_duration"));
        chrono.setCreatedAt(rs.getTimestamp("chrono_created_at").toInstant());

        chrono.setUser(userRowMapper.mapMinimalRow(rs, 1));

        return chrono;
    }
}
