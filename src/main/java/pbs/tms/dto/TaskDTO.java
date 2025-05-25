package pbs.tms.dto;

import lombok.Data;
import pbs.tms.entity.TaskStatus;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private String assignedUser;

    public TaskDTO(Long id, String title, String description, TaskStatus status, String username) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.assignedUser = username;
    }
}