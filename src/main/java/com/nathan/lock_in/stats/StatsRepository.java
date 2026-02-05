package com.nathan.lock_in.stats;

import com.nathan.lock_in.projects.ProjectsRowMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StatsRepository {

    private final ProjectsRowMapper projectsRowMapper;
    
    private final JdbcTemplate jdbcTemplate;
    private final FocusTrendsRowMapper focusTrendsRowMapper;
    private final ProjectStatsRowMapper projectStatsRowMapper;

    public List<FocusTrends> getTrendsInRange(String startDate, String userId){

        String query = """
                SELECT SUM(
                    CASE 
                        WHEN unit = 'second' THEN duration / 60
                        WHEN unit = 'minute' THEN duration
                        WHEN unit = 'hour' THEN duration * 60
                    END
                ) as total_focused, DATE(created_at) as date
                FROM chronos 
                WHERE id_user = ?::uuid, created_at > ?::timestamptz
                GROUP BY DATE(created_at)
                """;

        return jdbcTemplate.query(query, focusTrendsRowMapper, userId, startDate);
        
    }

    public List<ProjectStats> getStatsPerProject(String userId){
        String sql = """
                    SELECT 
                    p.id,
                    p.title,
                    SUM(
                        CASE 
                            WHEN c.unit = 'second' THEN c.duration / 60
                            WHEN c.unit = 'minute' THEN c.duration
                            WHEN c.unit = 'hour' THEN c.duration * 60
                        END
                    ) as total_minutes
                    FROM projects p
                    RIGHT JOIN chronos c ON c.id_project = p.id
                    WHERE c.id_user = ?::uuid
                    GROUP BY p.id, p.title
                """;   
        return jdbcTemplate.query(sql, projectStatsRowMapper, userId);
    }
}
