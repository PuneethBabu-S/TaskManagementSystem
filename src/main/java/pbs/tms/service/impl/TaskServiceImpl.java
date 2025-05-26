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
        Optional<User> user = Optional.ofNullable(getUser());
        user.ifPresent(task::setAssignedUser);
        taskRepository.save(task);
        return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getAssignedUser().getUsername());
    }

    @Override
    public TaskDTO updateTask(Long id,Task task) {
        Optional<User> user = Optional.ofNullable(getUser());
        Task updatedTask = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found."));
        if (user.isPresent() && (updatedTask.getAssignedUser().getUsername().equals(user.get().getUsername()) || user.get().getRole() == Role.ADMIN)) {
            updatedTask.setTitle(task.getTitle());
            updatedTask.setDescription(task.getDescription());
            updatedTask.setStatus(task.getStatus());
            taskRepository.save(updatedTask);
            return new TaskDTO(updatedTask.getId(), updatedTask.getTitle(), updatedTask.getDescription(), updatedTask.getStatus(), updatedTask.getAssignedUser().getUsername());
        }
        throw new RuntimeException("You don't have permission to edit this task.");
    }

    @Override
    public String deleteTask(Long id) {
        Optional<User> user = Optional.ofNullable(getUser());
        Task taskToDelete = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found."));
        if (user.isPresent() && (taskToDelete.getAssignedUser().getUsername().equals(user.get().getUsername()) || user.get().getRole() == Role.ADMIN)){
            taskRepository.delete(taskToDelete);
            return "Task deleted successfully.";
        }
        throw new RuntimeException("You don't have permission to delete this task.");
    }

    @Override
    public TaskDTO getTask(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent() && (task.get().getAssignedUser().getUsername().equals(getUser().getUsername()) || getUser().getRole() == Role.ADMIN)) {
            return new TaskDTO(task.get().getId(), task.get().getTitle(), task.get().getDescription(), task.get().getStatus(), task.get().getAssignedUser().getUsername());
        }
        throw new RuntimeException("Task not found.");
    }

    @Override
    public List<TaskDTO> getUserTasks() {
        Optional<User> user = Optional.ofNullable(getUser());
        if (user.isPresent()) {
            List<Task> tasks = taskRepository.findByAssignedUserId(user.get().getId());
            return tasks.stream().map(task -> new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getAssignedUser().getUsername())).toList();
        }
        throw new RuntimeException("User not found.");
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        if (getUser().getRole() == Role.ADMIN) {
            return taskRepository.findAll().stream().map(task -> new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getAssignedUser().getUsername())).toList();
        }
        throw new RuntimeException("You don't have permission to view all tasks.");
    }

    @Override
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }
}
