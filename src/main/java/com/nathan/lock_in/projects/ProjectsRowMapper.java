package com.nathan.lock_in.projects;

import com.nathan.lock_in.user.UserRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class ProjectsRowMapper implements RowMapper<Projects> {

    private final UserRowMapper userRowMapper;

    @Override
    public Projects mapRow(ResultSet rs, int rowNum) throws SQLException {

        Projects project = new Projects();

        project.setId(rs.getString("project_id"));
        project.setTitle(rs.getString("project_title"));
        project.setDescription(rs.getString("project_description"));
        project.setCreatedAt(rs.getTimestamp("project_created_at").toInstant());
        project.setUser(userRowMapper.mapMinimalRow(rs, 1));

        return project;

    }
}
