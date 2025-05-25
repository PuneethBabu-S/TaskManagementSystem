package pbs.tms.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import pbs.tms.entity.Task;
import pbs.tms.entity.User;
import pbs.tms.repository.TaskRepository;
import pbs.tms.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // 📝 Create a New Task
    @PostMapping("/create")
    public Task createTask(@RequestBody Task task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);

        user.ifPresent(task::setAssignedUser);
        return taskRepository.save(task);
    }

    // 📌 Get All Tasks for Logged-In User
    @GetMapping("/user")
    public List<Task> getUserTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(taskRepository::findByAssignedUser).orElse(List.of());
    }

    // ✏️ Update a Task (Only Assigned User or Admin)
    @PutMapping("update/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);

        return taskRepository.findById(id).map(task -> {
            if (task.getAssignedUser().equals(user.orElse(null))) {
                task.setTitle(updatedTask.getTitle());
                task.setDescription(updatedTask.getDescription());
                task.setStatus(updatedTask.getStatus());
                return taskRepository.save(task);
            }
            throw new RuntimeException("You don't have permission to edit this task.");
        }).orElseThrow(() -> new RuntimeException("Task not found."));
    }

    // 🗑 Delete a Task (Only Assigned User or Admin)
    @DeleteMapping("delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);

        return taskRepository.findById(id).map(task -> {
            if (task.getAssignedUser().equals(user.orElse(null))) {
                taskRepository.delete(task);
                return "Task deleted successfully.";
            }
            throw new RuntimeException("You don't have permission to delete this task.");
        }).orElseThrow(() -> new RuntimeException("Task not found."));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/tasks")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}