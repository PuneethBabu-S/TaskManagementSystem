package pbs.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pbs.tms.entity.Task;
import pbs.tms.entity.User;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedUser(User user);

    List<Task> findByAssignedUserId(Long id);
}