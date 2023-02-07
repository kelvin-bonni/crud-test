package com.example.scalability;

import org.aspectj.util.TypeSafeEnum;
import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@Controller
public class TaskController {
List<Task>
    private final TaskRepository repository;

    @Autowired
    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> createOne(@RequestBody TaskDto taskDto){
        Task task = new Task(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());

        Task savedTask = repository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(savedTask.getId());
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> readOne(@PathVariable Long id){
        if (repository.findById(id).isPresent())
            return ResponseEntity.status(HttpStatus.OK).body(repository.findById(id));
        else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<?> updateOne(@PathVariable Long id, @RequestBody TaskDto taskDto){
        HashSet<String> values = new HashSet<>();

        for (TaskStatus taskStatus : TaskStatus.values()) {
            values.add(taskStatus.name());
        }

        if(!values.contains(taskDto.getStatus()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Available statuses are: CREATED, APPROVED, REJECTED, BLOCKED, DONE.");

        Task retrievedTask = null;
        if (repository.findById(id).isPresent()){
            retrievedTask = repository.findById(id).get();
            retrievedTask.setTitle(taskDto.getTitle());
            retrievedTask.setDescription(taskDto.getDescription());
            retrievedTask.setTaskStatus(TaskStatus.valueOf(taskDto.getStatus()));
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Long id){
        Task retrievedTask = null;
        if (repository.findById(id).isPresent()){
            retrievedTask = repository.findById(id).get();
            repository.delete(retrievedTask);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> updateOne(){
        return ResponseEntity.status(HttpStatus.OK).body(repository.findAll());
    }

}
