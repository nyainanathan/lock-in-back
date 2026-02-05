package com.nathan.lock_in.stats;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectStatsRowMapper implements RowMapper<ProjectStats>{

    @Override
    public ProjectStats mapRow(ResultSet rs, int rowNum) throws SQLException{

        ProjectStats stat = new ProjectStats();
        
        String projectId = rs.getString("id") == null ? "Others" : rs.getString("id");

        String projectName = rs.getString("title") == null ? "Others" : rs.getString("title");

        stat.setId(projectId);
        stat.setName(projectName);
        stat.setFocusedMinutes(rs.getDouble("total_minutes"));
        
        return stat;
    }
}
