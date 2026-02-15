package com.nathan.lock_in.projects;

import com.nathan.lock_in.chronos.Chronos;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectsController {

    private final ProjectService projectService;

    @GetMapping("/")
    public ResponseEntity<?> getUserProjects(){
        try {
            List<Projects> projects = projectService.findUserProjects();
            return new  ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{projectId}/chronos")
    public ResponseEntity<?> getChronos(@PathVariable String projectId){
        try {
            List<Chronos> chronos = projectService.findByProjectId(projectId);
            return new ResponseEntity<>(chronos, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProject(@PathVariable String projectId){
        try{
            Projects project = projectService.findById(projectId);
            return new ResponseEntity<>(project, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createProject(@RequestBody ProjectCreationDTO project){
        try {
            Projects createdProject = projectService.create(project);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable String projectId){
        try {
            return new ResponseEntity<>(
                    projectService.delete(projectId),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PatchMapping("/dissociate/{chronoId}")
    public ResponseEntity<?> dissociateChrono(@PathVariable String chronoId){
        try{
            Chronos associated = projectService.dissociateChrono(chronoId);
            return new  ResponseEntity<>(associated, HttpStatus.OK);
        } catch (Exception e){
            return new  ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{projectId}/associate/{chronoId}")
    public ResponseEntity<?> associateChrono(@PathVariable String projectId, @PathVariable String chronoId){
        try{
            Chronos associated = projectService.associateChrono(chronoId, projectId);
            return new  ResponseEntity<>(associated, HttpStatus.OK);
        } catch (Exception e){
            return new  ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable String projectId, @RequestBody ProjectUpdateDTO project){
        try {
            Projects updatedProjects = projectService.update(project,  projectId);
            return new ResponseEntity<>(updatedProjects, HttpStatus.OK);
        } catch (Exception e) {
            return new  ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
