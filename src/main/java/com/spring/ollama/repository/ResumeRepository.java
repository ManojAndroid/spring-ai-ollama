package com.spring.ollama.repository;
import com.spring.ollama.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {
    ResumeEntity findByFileName(String fileName);
}
