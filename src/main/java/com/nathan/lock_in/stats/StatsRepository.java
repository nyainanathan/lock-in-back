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
    private final StreakStatsRowMapper streakStatsRowMapper;

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
                WHERE id_user = ?::uuid AND created_at > ?::timestamp
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


    public ProjectStats getLastProject(String userId) {
        String sql = """
                    SELECT
                        p.id,
                        p.title,
                        MAX(c.created_at) as latest_created_at,
                        p.id_user,
                        SUM(
                            CASE
                                WHEN c.unit = 'second' THEN c.duration / 60
                                WHEN c.unit = 'minute' THEN c.duration
                                WHEN c.unit = 'hour' THEN c.duration * 60
                            END
                        ) as total_minutes
                    FROM projects p
                    LEFT JOIN chronos c ON c.id_project = p.id
                    WHERE p.id_user = ?::uuid
                    GROUP BY p.id, p.title, p.id_user
                    ORDER BY MAX(c.created_at) DESC
                    LIMIT 1;
                """;
        return jdbcTemplate.queryForObject(sql, projectStatsRowMapper, userId);
    }

    public StreakStats getUserStreak(String userId){
        String sql = """
                WITH user_dates AS (
                    SELECT DISTINCT
                        id_user,
                        DATE(created_at) as activity_date
                    FROM chronos
                    where id_user = ?::uuid
                ),
                streak_groups AS (
                    SELECT 
                        id_user,
                        activity_date,
                        activity_date - (ROW_NUMBER() OVER (PARTITION BY id_user ORDER BY activity_date))::int AS streak_group
                    FROM user_dates
                ),
                streaks AS (
                    SELECT 
                        id_user,
                        streak_group,
                        MIN(activity_date) as streak_start,
                        MAX(activity_date) as streak_end,
                        COUNT(*) as streak_length,
                        MAX(activity_date) = CURRENT_DATE OR MAX(activity_date) = CURRENT_DATE - 1 as is_current
                    FROM streak_groups
                    GROUP BY id_user, streak_group
                )
                SELECT 
                    id_user,
                    MAX(streak_length) as biggest_streak,
                    MAX(CASE WHEN is_current THEN streak_length ELSE 0 END) as current_streak
                FROM streaks
                GROUP BY id_user
                """;

                return jdbcTemplate.queryForObject(sql,streakStatsRowMapper, userId );
    }
}
