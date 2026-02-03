package com.nathan.lock_in.chronos;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChronosRepository {

    private final JdbcTemplate jdbcTemplate;

    public Chronos findById(String id) {

    }

    public Chronos save(ChronosCreationDTO toSave) {

        String sql = """
                INSERT INTO chronos
                (id_user, duration, title, unit)
                VALUES (?::UUID, ?, ?, ?::duration_unit)
                ON CONFLICT DO UPDATE
                SET duration = EXCLUDED.duration,
                title = EXCLUDED.title,
                unit = EXCLUDED.unit
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
    }
}
