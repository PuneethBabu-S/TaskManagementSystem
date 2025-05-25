package pbs.tms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pbs.tms.dto.TaskDTO;
import pbs.tms.entity.Role;
import pbs.tms.entity.Task;
import pbs.tms.entity.User;
import pbs.tms.repository.TaskRepository;
import pbs.tms.repository.UserRepository;
import pbs.tms.service.TaskService;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public TaskDTO createTask(Task task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);

        user.ifPresent(task::setAssignedUser);
        taskRepository.save(task);
        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getAssignedUser().getUsername());

    }

    @Override
    public TaskDTO updateTask(Long id,Task task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);
        Task updatedTask = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found."));
        if (user.isPresent() && (updatedTask.getAssignedUser().getUsername().equals(username) || user.get().getRole() == Role.ADMIN)) {
            updatedTask.setTitle(task.getTitle());
            updatedTask.setDescription(task.getDescription());
            updatedTask.setStatus(task.getStatus());
            taskRepository.save(updatedTask);
            return new TaskDTO(updatedTask.getId(), updatedTask.getTitle(), updatedTask.getDescription(), updatedTask.getStatus(), updatedTask.getAssignedUser().getUsername());
        }
        throw new RuntimeException("You don't have permission to edit this task.");
    }

    @Override
    public TaskDTO deleteTask(Task task) {
        return null;
    }

    @Override
    public TaskDTO getTask(Long id) {
        return null;
    }

    @Override
    public List<TaskDTO> getUserTasks(Long id) {
        return List.of();
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return List.of();
    }
}
