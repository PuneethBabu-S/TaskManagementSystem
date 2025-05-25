package pbs.tms.service;

import java.util.List;

import pbs.tms.dto.TaskDTO;
import pbs.tms.entity.Task;

public interface TaskService {
    public TaskDTO createTask(Task task);
    public TaskDTO updateTask(Long id,Task task);
    public TaskDTO deleteTask(Task task);
    public TaskDTO getTask(Long id);
    public List<TaskDTO> getUserTasks(Long id);
    public List<TaskDTO> getAllTasks();
}
