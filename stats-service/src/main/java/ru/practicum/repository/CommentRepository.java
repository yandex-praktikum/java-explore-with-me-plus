package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Comment;
import ru.practicum.model.CommentStatus;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByEventIdAndStatus(Long eventId, CommentStatus status, Pageable pageable);

    Optional<Comment> findByIdAndEventIdAndAuthorId(Long commentId, Long eventId, Long authorId);

    List<Comment> findByStatus(CommentStatus status, Pageable pageable);

    List<Comment> findByAuthorId(Long authorId, Pageable pageable);

    List<Comment> findByEventIdAndAuthorId(Long eventId, Long authorId);

    boolean existsByEventIdAndAuthorId(Long eventId, Long authorId);


    Optional<Comment> findByIdAndEventId(Long commentId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, CommentStatus status);

    List<Comment> findByEventId(Long eventId);

    boolean existsByEventIdAndAuthorIdAndStatus(Long eventId, Long authorId, CommentStatus status);
}
