package com.nathan.lock_in.user;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepository {

    private JdbcTemplate jdbcTemplate;
    private UserRowMapper userRowMapper;

    public List<Users> findAll(){
        String query = """
                SELECT id as user_id,
                first_name as user_first_name,
                last_name as user_last_name,
                email as user_email,
                created_at as user_created_at,
                password_hash as user_password_hash
                FROM users
                """;

        return jdbcTemplate.query(query, userRowMapper);
    }

    public Optional<Users> findByEmail(String email){
        String query = """
                SELECT id as user_id,
                first_name as user_first_name,
                last_name as user_last_name,
                email as user_email,
                created_at as user_created_at,
                password_hash as user_password_hash
                FROM users
                WHERE email = ?
                """;
        try{
                return Optional.ofNullable(
                        jdbcTemplate.queryForObject(query, userRowMapper, email)
                ) ;

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Users findById(String id){
        String query = """
                SELECT id as user_id,
                first_name as user_first_name,
                last_name as user_last_name,
                email as user_email,
                created_at as user_created_at,
                password_hash as user_password_hash
                FROM users
                WHERE id = ?::UUID
                """;

        return jdbcTemplate.queryForObject(query, userRowMapper, id);
    }

    public Users save(UserCreationDTO newUser){
        String query = """
                INSERT INTO users (first_name, last_name, email, password_hash)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (email) DO UPDATE
                SET first_name = excluded.first_name,
                    last_name = excluded.last_name,
                    password_hash = excluded.password_hash
                RETURNING id
                """;

        String newUserId = jdbcTemplate.queryForObject(
                query,
                new Object[] {
                        newUser.getFirstName(), newUser.getLastName(),
                        newUser.getEmail(), newUser.getPasswordHash()
                },
                String.class
        );

        return findById(newUserId);
    }

}
