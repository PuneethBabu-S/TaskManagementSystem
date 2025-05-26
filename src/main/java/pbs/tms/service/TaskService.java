package pbs.tms.service;

import java.util.List;

import pbs.tms.dto.TaskDTO;
import pbs.tms.entity.Task;
import pbs.tms.entity.User;

public interface TaskService {
    public TaskDTO createTask(Task task);
    public TaskDTO updateTask(Long id,Task task);
    public String deleteTask(Long id);
    public TaskDTO getTask(Long id);
    public List<TaskDTO> getUserTasks();
    public List<TaskDTO> getAllTasks();
    public User getUser();
}
