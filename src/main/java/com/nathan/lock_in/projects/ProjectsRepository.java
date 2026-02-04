package com.nathan.lock_in.projects;

import com.nathan.lock_in.chronos.Chronos;
import com.nathan.lock_in.chronos.ChronosRepository;
import com.nathan.lock_in.chronos.ChronosRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProjectsRowMapper projectsRowMapper;
    private final ChronosRepository chronosRepository;
    private final ChronosRowMapper chronosRowMapper;

    public Projects findById(String id){
        String sql = """
                SELECT p.id as project_id,
                p.title as project_title,
                p.description as project_description,
                p.created_at as project_created_at,
                u.id as user_id,
                u.first_name as user_first_name,
                u.last_name as user_last_name,
                u.email as user_email
                FROM projects AS p
                JOIN users AS u
                ON p.id_user = u.id
                WHERE p.id = ?::uuid
                """;

        return jdbcTemplate.queryForObject(sql, projectsRowMapper, id);
    }

    public Projects create(ProjectCreationDTO toSave){
        String sql = """
                INSERT INTO projects
                (id_user, title, description)
                VALUES (?::uuid, ?, ?)
                RETURNING id
                """;

        String projectId = jdbcTemplate.queryForObject(
                sql,
                new Object[]{
                        toSave.getUserId(),
                        toSave.getTitle(),
                        toSave.getDescription()
                },
                String.class
        );

        return findById(projectId);
    }

    public Projects update(String projectId, String query, String[] params){
        jdbcTemplate.update(query, params);
        return findById(projectId);
    }

    public Projects delete(String projectId){
        String sql = """
                DELETE FROM projects WHERE id = ?::uuid
                """;

        Projects toBeDeleted = findById(projectId);

        jdbcTemplate.update(sql, projectId);

        return toBeDeleted;
    }

    public List<Projects> findUserProjects(String userId) {
        String sql = """
                SELECT p.id as project_id,
                p.title as project_title,
                p.description as project_description,
                p.created_at as project_created_at,
                u.id as user_id,
                u.first_name as user_first_name,
                u.last_name as user_last_name,
                u.email as user_email
                FROM projects AS p
                JOIN users AS u
                ON p.id_user = u.id
                WHERE u.id = ?::uuid
                """;

        return jdbcTemplate.query(
                sql,
                projectsRowMapper,
                userId
        );
    }

    public Chronos associateToProject(String chronoId, String projectId){
        String sql = """
                UPDATE chronos SET id_project = ?::uuid WHERE id=?::uuid
                """;
        jdbcTemplate.update(sql, projectId, chronoId);

        return chronosRepository.findById(chronoId);
    }
    public Chronos dissociateToProject(String chronoId){
        String sql = """
                UPDATE chronos SET id_project = NULL WHERE id = ?::uuid
                """;
        jdbcTemplate.update(sql, chronoId);
        return chronosRepository.findById(chronoId);
    }

    public List<Chronos> findChronosByProject(String projectId){
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
                WHERE p.id = ?::uuid
                """;
        return jdbcTemplate.query(sql, chronosRowMapper,projectId);
    }
}
