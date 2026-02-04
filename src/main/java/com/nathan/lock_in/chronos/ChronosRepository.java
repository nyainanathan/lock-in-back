package com.nathan.lock_in.chronos;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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
                p.description as project_description,
                p.created_at as project_created_at
                FROM chronos AS c
                JOIN users AS u ON c.id_user = u.id
                LEFT JOIN projects as p ON c.id_project = p.id
                WHERE c.id = ?::uuid
                """;

        return jdbcTemplate.queryForObject(sql, chronosRowMapper, id);
    }

    public Chronos save(String query, Object[] params) {

        String newChronoId = jdbcTemplate.queryForObject(
                query,
                String.class,
                params
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
                p.description as project_description,
                p.created_at as project_created_at
                FROM chronos AS c
                JOIN users AS u ON c.id_user = u.id
                LEFT JOIN projects AS p ON p.id = c.id_project
                WHERE c.id_user= ?::uuid
                LIMIT ? OFFSET ?;
                """;

        return jdbcTemplate.query(sql, chronosRowMapper, userId, size, offset);
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
