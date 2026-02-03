package com.nathan.lock_in.chronos;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChronosService {

    private final ChronosRepository chronosRepository;

    public Chronos save(ChronosCreationDTO toSave){
        return chronosRepository.save(toSave);
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

    public Chronos update(ChronosUpdate updatedChrono) {
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
