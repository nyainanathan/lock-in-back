package com.nathan.lock_in.stats;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class StreakStatsRowMapper implements RowMapper<StreakStats>{

    public StreakStats mapRow(ResultSet rs, int rowNum) throws SQLException{

        StreakStats stat = new StreakStats();

        stat.setIdUser(rs.getString("id_use"));
        stat.setBiggestStreak(rs.getInt("biggest_streak"));
        stat.setCurrentStreak(rs.getInt("current_streak"));

        return stat;
    };
}
