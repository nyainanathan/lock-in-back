package com.nathan.lock_in.stats;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StatsRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private final FocusTrendsRowMapper focusTrendsRowMapper;

    public List<FocusTrends> getTrendsInRange(String startDate, String userId){

        String query = """
                SELECT SUM(duration) as total_focused, created_at
                FROM chronos 
                WHERE id_user = ?::uuid, created_at > ?::timestamptz
                GROUP BY created_at
                """;

        return jdbcTemplate.query(query, focusTrendsRowMapper, userId, startDate);
        
    }
}
