package com.nathan.lock_in.chronos;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChronosRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ChronosRowMapper chronosRowMapper;

    public Chronos findById(String id) {

        String sql = """
                SELECT c.id as chrono_id,
                c.duration as chrono_duration,
                c.title as chrono_title,
                c.created_at as chrono_created_at,
                c.unit as chrono_unit,
                u.id as user_id,
                u.first_name as user_first_name,
                u.last_name as user_last_name,
                u.email as user_email,
                p.id as project_id,
                p.title as project_title,
                p.description as project_description
                FROM chronos AS c
                JOIN users AS u ON c.id_user = u.id
                JOIN projects as p ON p.id = c.id_user
                WHERE c.id = CAST(? AS uuid)
                """;

        return jdbcTemplate.queryForObject(sql, chronosRowMapper, id);
    }

    public Chronos save(String query, Object[] params) {

        String newChronoId = jdbcTemplate.queryForObject(
                query,
                params,
                String.class
        );

        return findById(newChronoId);
    }

    public List<Chronos> findByUserId(String userId, int size, int offset){
        String sql = """
                SELECT c.id as chrono_id,
                c.duration as chrono_duration,
                c.title as chrono_title,
                c.created_at as chrono_created_at,
                c.unit as chrono_unit,
                u.id as user_id,
                u.first_name as user_first_name,
                u.last_name as user_last_name,
                u.email as user_email,
                p.id as project_id,
                p.title as project_title,
                p.description as project_description
                FROM chronos AS c
                JOIN users AS u ON c.id_user = u.id
                JOIN projects as p ON p.id = c.id_user
                WHERE u.id = CAST(? AS uuid)
                OFFSET ? LIMIT ?
                """;

        return jdbcTemplate.query(sql, chronosRowMapper, userId, offset, size);
    }

    public Chronos deleteById(String chronoId){
        String sql = """
                DELETE FROM chronos WHERE id = CAST(? AS uuid)
                """;

        Chronos toBeDeleted = findById(chronoId);

        jdbcTemplate.update(sql, chronoId);

        return toBeDeleted;
    }

    public Chronos update(String query, Object[] args, String id){
        jdbcTemplate.update(query, args);
        return findById(id);
    }
}
