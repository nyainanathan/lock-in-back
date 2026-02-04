package com.nathan.lock_in.projects;

import com.nathan.lock_in.chronos.Chronos;
import com.nathan.lock_in.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectsRepository projectsRepository;

    public Projects findById(String projectId) {
        return projectsRepository.findById(projectId);
    }

    public List<Projects> findUserProjects(){
        String userId = getUserId();
        return projectsRepository.findUserProjects(userId);

    }

    public Projects create(ProjectCreationDTO toCreate) {
        String userId = getUserId();
        toCreate.setUserId(userId);
        return projectsRepository.create(toCreate);
    }

    public Projects update(ProjectUpdateDTO data, String projectId) {
        StringBuilder query = new StringBuilder("UPDATE projects SET");

        List<String> params = new ArrayList<>();

        int queryIndex = 0;
        if(data.getTitle() != null) {
            queryIndex++;
            params.add(data.getTitle());
            query.append(" title = ?");
        }

        if(data.getDescription() != null){
            if(queryIndex != 0) {
                query.append(", ");
            }
            query.append(" description = ?");
            params.add(data.getDescription());
        }

        query.append(" WHERE id = ?::uuid");
        params.add(projectId);

        return projectsRepository.update(projectId, query.toString(), params.toArray(new String[0]));
    }

    public Projects delete(String projectId) {
        return projectsRepository.delete(projectId);
    }

    private String getUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        String userId = user.getId();
        return userId;
    }

    public Chronos associateChrono(String chronoId, String projectId){
        return projectsRepository.associateToProject(chronoId, projectId);
    }

    public Chronos dissociateChrono(String chronoId){
        return projectsRepository.dissociateToProject(chronoId);
    }

    public List<Chronos> findByProjectId(String projectId){
        return projectsRepository.findChronosByProject(projectId);
    }

}

