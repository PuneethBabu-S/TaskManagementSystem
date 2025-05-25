package pbs.tms.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import pbs.tms.dto.TaskDTO;
import pbs.tms.entity.Task;
import pbs.tms.entity.User;
import pbs.tms.repository.TaskRepository;
import pbs.tms.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pbs.tms.service.TaskService;

import java.util.List;
import java.util.Optional;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskService taskService;

    // 📝 Create a New Task
    @PostMapping("/create")
    public ResponseEntity<TaskDTO> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    // 📌 Get All Tasks for Logged-In User
    @GetMapping("/user")
    public List<Task> getUserTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(taskRepository::findByAssignedUser).orElse(List.of());
    }

    // ️ Update a Task (Only Assigned User or Admin)
    @PutMapping("update/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        return ResponseEntity.ok(taskService.updateTask(id, updatedTask));
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