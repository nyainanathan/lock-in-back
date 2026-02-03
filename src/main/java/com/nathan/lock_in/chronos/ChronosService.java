package com.nathan.lock_in.chronos;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChronosService {

    private final ChronosRepository chronosRepository;

    private Chronos save(ChronosCreationDTO toSave){
        return chronosRepository.save(toSave);
    }

    private List<Chronos> findAll(String userId, Integer size, Integer page){

        if(size == null) {
            size = 10;
        }

        if(page == null) {
            page = 1;
        }
        int offset = (page - 1 ) * size;

        return chronosRepository.findByUserId(userId, size, offset);
    }
}
