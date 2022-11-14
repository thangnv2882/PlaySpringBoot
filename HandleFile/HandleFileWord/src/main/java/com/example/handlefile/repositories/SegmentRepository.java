package com.example.handlefile.repositories;

import com.example.handlefile.entity.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SegmentRepository extends JpaRepository<Segment, Long> {
    List<Segment> findSegmentByDocumentId(Long idDocument);
}
