package com.nathan.lock_in.projects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ProjectCreationDTO {
    private String userId;
    private String title;
    private String description;
}
