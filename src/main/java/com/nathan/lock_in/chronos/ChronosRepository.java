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
                u.email as user_email
                FROM chronos AS c
                JOIN users AS u ON c.id_user = u.id
                WHERE c.id = ?
                """;

        return jdbcTemplate.queryForObject(sql, chronosRowMapper, id);
    }

    public Chronos save(ChronosCreationDTO toSave) {

        String sql = """
                INSERT INTO chronos
                (id_user, duration, title, unit)
                VALUES (?::UUID, ?, ?, ?::duration_unit)
                ON CONFLICT DO NOTHING
                RETURNING id
                """;

        String newChronoId = jdbcTemplate.queryForObject(
                sql,
                new Object[] {
                        toSave.getUserId(), toSave.getDuration(),
                        toSave.getTitle(), toSave.getUnit()
                },
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
                u.email as user_email
                FROM chronos AS c
                JOIN users AS u ON c.id_user = u.id
                WHERE u.id = ?
                OFFSET ? LIMIT ?
                """;

        return jdbcTemplate.query(sql, chronosRowMapper, userId, offset, size);
    }
}
