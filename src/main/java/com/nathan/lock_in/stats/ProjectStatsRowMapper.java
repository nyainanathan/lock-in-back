package com.nathan.lock_in.stats;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectStatsRowMapper implements RowMapper<ProjectStats>{

    @Override
    public ProjectStats mapRow(ResultSet rs, int rowNum) throws SQLException{

        ProjectStats stat = new ProjectStats();
        
        // String handling - rs.getString() returns null if column is NULL
        String projectId = rs.getString("id");
        if (projectId == null) {
            projectId = "Others";
        }

        String projectName = rs.getString("title");
        if (projectName == null) {
            projectName = "Others";
        }

        // For numeric types, use getObject() to check for null first
        BigDecimal focusedMinutesBD = rs.getBigDecimal("total_minutes");
        Double focusedMinutes = (focusedMinutesBD != null) ? focusedMinutesBD.doubleValue() : 0.0;

        stat.setId(projectId);
        stat.setName(projectName);
        stat.setFocusedMinutes(focusedMinutes);

        System.out.println(stat);

        return stat;
    }
}