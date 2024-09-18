package ru.practicum.server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.model.Hit;

public interface HitRepository extends JpaRepository<Hit, Integer> {
}
