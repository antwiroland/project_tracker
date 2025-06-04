package org.sikawofie.projecttracker.repository;

import org.sikawofie.projecttracker.entity.Developer;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    @Query("SELECT d FROM Developer d ORDER BY SIZE(d.tasks) DESC")
    List<Developer> findTop5ByTaskCount(SpringDataWebProperties.Pageable pageable);
}
