package com.nathan.lock_in.chronos;

import com.nathan.lock_in.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChronosService {

    private final ChronosRepository chronosRepository;

    public Chronos save(ChronosCreationDTO toSave){
        List<Object> params = new ArrayList<>();
        params.add(UUID.fromString(toSave.getUserId()));
        params.add(toSave.getDuration());
        params.add(toSave.getTitle());
        params.add(toSave.getUnit().name());
        params.add(Timestamp.from(toSave.getCreatedAt()));
        params.add(Timestamp.from(toSave.getStoppedAt()));
        String query;

        if (toSave.getProjectId() != null) {
            params.add(UUID.fromString(toSave.getProjectId()));
            query = """
            INSERT INTO chronos
            (id_user, duration, title, unit, created_at, stopped_at, id_project)
            VALUES (?, ?, ?, CAST(? AS duration_unit), ?, ?, ?)
            RETURNING id
            """;
        } else {
            query = """
            INSERT INTO chronos
            (id_user, duration, title, unit)
            VALUES (?, ?, ?, CAST(? AS duration_unit), ?, ?)
            RETURNING id
            """;
        }

        return chronosRepository.save(query, params.toArray(new Object[0]));
    }

    public List<Chronos> findAll(String userId, Integer size, Integer page){

        if(size == null) {
            size = 10;
        }

        if(page == null) {
            page = 1;
        }
        int offset = (page - 1 ) * size;

        return chronosRepository.findByUserId(userId, size, offset);
    }

    public Chronos update(ChronosUpdate updatedChrono) throws Exception {

        if(updatedChrono.getCreatedAt() != null && updatedChrono.getStoppedAt() != null) {
            if(updatedChrono.getStoppedAt().isBefore(updatedChrono.getCreatedAt())){
                throw new Exception("Chrono : the start time cannot be after the end time.");
            }

            long minutesBetweenThem = Duration.between(
                    updatedChrono.getCreatedAt(),
                    updatedChrono.getStoppedAt()
            ).toMinutes();

            double focusTime = 0;

            switch (updatedChrono.getUnit()) {
                case hour -> focusTime = (double) (updatedChrono.getDuration() * 60);
                case minute -> focusTime =  (updatedChrono.getDuration());
                case second -> focusTime = (double) (updatedChrono.getDuration() / 60);
            }

            if(focusTime > minutesBetweenThem) {
                throw new Exception("Chrono : duration cannot exceed the time between the start and the end.");
            }
        }

        List<Object> params = new ArrayList<>();

        String firstPart = """
                UPDATE chronos
                """;
        StringBuilder middlePart = new StringBuilder();
        int paramsNumber = 0;

        if(updatedChrono.getUnit() != null) {
            middlePart.append("SET unit = ?::public.duration_unit");
            paramsNumber++;
            params.add(updatedChrono.getUnit().toString());
        }

        if(updatedChrono.getDuration() != null) {
            if(paramsNumber == 0){
                middlePart.append("SET ");
            } else {
                middlePart.append(", ");
            }
            middlePart.append("duration = ?");
            paramsNumber++;
            params.add(updatedChrono.getDuration());
        }

        if(updatedChrono.getTitle()!= null) {
            if(paramsNumber == 0){
                middlePart.append("SET ");
            } else {
                middlePart.append(", ");
            }
            middlePart.append(" title = ?");
            paramsNumber++;
            params.add(updatedChrono.getTitle());
        }

        if(updatedChrono.getCreatedAt() != null) {
            if(paramsNumber == 0){
                middlePart.append("SET ");
            } else {
                middlePart.append(", ");
            }
            middlePart.append("created_at = ?");
            paramsNumber++;
            params.add(Timestamp.from(updatedChrono.getCreatedAt()));
        }

        if(updatedChrono.getStoppedAt() != null) {
            if(paramsNumber == 0){
                middlePart.append("SET ");
            } else {
                middlePart.append(", ");
            }
            middlePart.append("stopped_at = ?");
            params.add(Timestamp.from(updatedChrono.getStoppedAt()));
        }

        String finalPart = """
                 WHERE id = ?::uuid
                """;

        params.add(updatedChrono.getId());

        String query = firstPart
                + middlePart + finalPart;

        return chronosRepository.update(query, params.toArray(), updatedChrono.getId());
    }

    public Chronos delete(String id){
        return chronosRepository.deleteById(id);
    }

    public List<Chronos> getChronosBetweenDates(Instant start, Instant end) throws Exception {

        if(start != null &&  end != null && start.isAfter(end)) {
            throw new Exception("Starting date cannot be after the ending date");
        }

        String sql = """
                SELECT c.id as chrono_id,
                c.duration as chrono_duration,
                c.title as chrono_title,
                c.created_at as chrono_created_at,
                c.stopped_at as chrono_stopped_at,
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
                WHERE 1 = 1
                """;

        StringBuilder sqlBuilder = new StringBuilder();
        List<Object>  params = new ArrayList<>();

        if(start != null) {
            sqlBuilder.append(" AND c.created_at >= ?");
            params.add(Timestamp.from(start));
        }
        if(end != null) {
            sqlBuilder.append(" AND c.created_at <= ?");
            params.add(Timestamp.from(end));
        }

        sqlBuilder.append(" AND c.id_user = ?::uuid");
        params.add(getUserId());

        String finalSql = sql + sqlBuilder;

        return chronosRepository.getChronosBetweenDates(finalSql, params.toArray());
    }

    private String getUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return user.getId();
    }
}
