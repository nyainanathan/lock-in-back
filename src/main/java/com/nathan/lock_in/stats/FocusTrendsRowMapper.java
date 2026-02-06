package com.nathan.lock_in.stats;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class FocusTrendsRowMapper implements RowMapper<FocusTrends> {

    @Override
    public FocusTrends mapRow(ResultSet rs, int rowNum) throws SQLException{
        FocusTrends trends = new FocusTrends();
        String createdAt = rs.getString("date");
        trends.setDate(LocalDate.parse(createdAt));
        trends.setFocusedMinutes(rs.getDouble("total_focused"));
        return trends;
    }
    
}