package com.nathan.lock_in.chronos;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
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
}
