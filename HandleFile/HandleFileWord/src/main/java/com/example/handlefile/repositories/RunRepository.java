package com.example.handlefile.repositories;

import com.example.handlefile.entity.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RunRepository extends JpaRepository<Run, Long> {

    List<Run> findAllByOrderByIdRun();

}