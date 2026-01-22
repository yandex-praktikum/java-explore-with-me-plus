package ru.practicum.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CommentAdminRequest;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.CommentStatus;
import ru.practicum.model.User;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;


    public List<CommentDto> getPublishedComments(Long eventId, int from, int size) {
        validateId(eventId, "ID события");

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return commentRepository.findByEventIdAndStatus(
                        eventId, CommentStatus.PUBLISHED, pageable)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    public CommentDto getPublishedComment(Long eventId, Long commentId) {
        validateId(eventId, "ID события");
        validateId(commentId, "ID комментария");

        Comment comment = commentRepository.findByIdAndEventId(commentId, eventId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (comment.getStatus() != CommentStatus.PUBLISHED) {
            throw new NotFoundException("Комментарий не опубликован");
        }

        return commentMapper.toDto(comment);
    }


    public Long getPublishedCommentsCount(Long eventId) {
        validateId(eventId, "ID события");
        return commentRepository.countByEventIdAndStatus(eventId, CommentStatus.PUBLISHED);
    }


    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        validateId(userId, "ID пользователя");
        validateId(eventId, "ID события");
        validateCommentText(newCommentDto.getText());

        User user = getUserOrThrow(userId);
        Event event = getPublishedEventOrThrow(eventId);

        checkIfUserCanComment(userId, eventId);

        Comment comment = buildNewComment(newCommentDto, user, event);
        Comment savedComment = commentRepository.save(comment);

        event.incrementCommentCount();
        eventRepository.save(event);

        log.info("Создан комментарий id={} к событию id={} от пользователя id={}",
                savedComment.getId(), eventId, userId);

        return commentMapper.toDto(savedComment);
    }


    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, Long commentId,
                                    NewCommentDto updateCommentDto) {
        validateId(userId, "ID пользователя");
        validateId(eventId, "ID события");
        validateId(commentId, "ID комментария");
        validateCommentText(updateCommentDto.getText());

        Comment comment = getCommentForUpdateOrThrow(commentId, eventId, userId);
        validateCommentForUpdate(comment);

        comment.setText(updateCommentDto.getText());
        comment.setEdited(LocalDateTime.now());


        if (comment.getStatus() == CommentStatus.PUBLISHED) {
            comment.setStatus(CommentStatus.PENDING);
        }

        Comment updatedComment = commentRepository.save(comment);
        log.info("Обновлен комментарий id={}", commentId);

        return commentMapper.toDto(updatedComment);
    }


    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        validateId(userId, "ID пользователя");
        validateId(eventId, "ID события");
        validateId(commentId, "ID комментария");

        Comment comment = commentRepository.findByIdAndEventIdAndAuthorId(commentId, eventId, userId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        CommentStatus oldStatus = comment.getStatus();

        comment.setStatus(CommentStatus.DELETED);
        commentRepository.save(comment);

        if (oldStatus == CommentStatus.PUBLISHED) {
            Event event = comment.getEvent();
            event.decrementCommentCount();
            eventRepository.save(event);
        }

        log.info("Удален комментарий id={} пользователем id={}", commentId, userId);
    }


    public List<CommentDto> getUserCommentsForEvent(Long userId, Long eventId) {
        validateId(userId, "ID пользователя");
        validateId(eventId, "ID события");

        return commentRepository.findByEventIdAndAuthorId(eventId, userId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }


    public boolean canUserCommentEvent(Long userId, Long eventId) {
        validateId(userId, "ID пользователя");
        validateId(eventId, "ID события");

        return !commentRepository.existsByEventIdAndAuthorId(eventId, userId);
    }


    public List<CommentDto> getPendingComments(int from, int size) {
        validatePagination(from, size);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "created"));
        return commentRepository.findByStatus(CommentStatus.PENDING, pageable)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public CommentDto moderateComment(Long commentId, CommentAdminRequest request) {
        validateId(commentId, "ID комментария");

        if (request.getStatus() == null) {
            throw new ForbiddenException("Статус комментария не может быть null");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + commentId + " не найден"));

        CommentStatus oldStatus = comment.getStatus();
        comment.setStatus(request.getStatus());
        comment.setModeratorMessage(request.getModeratorMessage());

        Comment moderatedComment = commentRepository.save(comment);

        updateEventCommentCountAfterModeration(comment.getEvent(), oldStatus, request.getStatus());

        log.info("Модерирован комментарий id={}, статус: {}", commentId, request.getStatus());

        return commentMapper.toDto(moderatedComment);
    }

    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        validateId(userId, "ID пользователя");
        validatePagination(from, size);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return commentRepository.findByAuthorId(userId, pageable)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getCommentsByStatus(CommentStatus status, int from, int size) {
        if (status == null) {
            throw new ForbiddenException("Статус не может быть null");
        }
        validatePagination(from, size);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return commentRepository.findByStatus(status, pageable)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        validateId(commentId, "ID комментария");

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));


        CommentStatus oldStatus = comment.getStatus();
        commentRepository.delete(comment);


        if (oldStatus == CommentStatus.PUBLISHED) {
            Event event = comment.getEvent();
            event.decrementCommentCount();
            eventRepository.save(event);
        }

        log.info("Администратором удален комментарий id={}", commentId);
    }


    public List<CommentDto> getAllCommentsForEvent(Long eventId, int from, int size) {
        validateId(eventId, "ID события");
        validatePagination(from, size);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return commentRepository.findByEventId(eventId)
                .stream()
                .skip(from)
                .limit(size)
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }


    public List<CommentDto> searchComments(String searchText, int from, int size) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new ForbiddenException("Текст для поиска не может быть пустым");
        }
        validatePagination(from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> pendingComments = commentRepository.findByStatus(CommentStatus.PUBLISHED, pageable);

        return pendingComments.stream()
                .filter(comment -> comment.getText().toLowerCase().contains(searchText.toLowerCase()))
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }


    public List<CommentDto> getRecentComments(int hours, int limit) {
        if (hours <= 0) {
            throw new ForbiddenException("Количество часов должно быть положительным");
        }
        if (limit <= 0 || limit > 100) {
            throw new ForbiddenException("Лимит должен быть от 1 до 100");
        }

        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "created"));

        return commentRepository.findByStatus(CommentStatus.PUBLISHED, pageable)
                .stream()
                .filter(comment -> comment.getCreated().isAfter(since))
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Event getPublishedEventOrThrow(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Нельзя комментировать неопубликованное событие");
        }

        return event;
    }

    private void checkIfUserCanComment(Long userId, Long eventId) {
        if (commentRepository.existsByEventIdAndAuthorId(eventId, userId)) {
            throw new ForbiddenException("Вы уже оставляли комментарий к этому событию");
        }
    }

    private void validateCommentText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new ForbiddenException("Текст комментария не может быть пустым");
        }

        text = text.trim();

        if (text.length() < 10) {
            throw new ForbiddenException("Текст комментария должен быть не менее 10 символов");
        }

        if (text.length() > 2000) {
            throw new ForbiddenException("Текст комментария должен быть не более 2000 символов");
        }
    }

    private Comment getCommentForUpdateOrThrow(Long commentId, Long eventId, Long userId) {
        return commentRepository.findByIdAndEventIdAndAuthorId(commentId, eventId, userId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
    }

    private void validateCommentForUpdate(Comment comment) {
        if (comment.getStatus() == CommentStatus.REJECTED) {
            throw new ForbiddenException("Нельзя редактировать отклоненный комментарий");
        }

        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new ForbiddenException("Комментарий был удален");
        }
    }

    private Comment buildNewComment(NewCommentDto newCommentDto, User user, Event event) {
        return Comment.builder()
                .text(newCommentDto.getText().trim())
                .event(event)
                .author(user)
                .status(CommentStatus.PENDING)
                .created(LocalDateTime.now())
                .build();
    }

    private void updateEventCommentCountAfterModeration(Event event, CommentStatus oldStatus, CommentStatus newStatus) {
        if (oldStatus == CommentStatus.PUBLISHED && newStatus != CommentStatus.PUBLISHED) {

            event.decrementCommentCount();
            eventRepository.save(event);
        } else if (oldStatus != CommentStatus.PUBLISHED && newStatus == CommentStatus.PUBLISHED) {

            event.incrementCommentCount();
            eventRepository.save(event);
        }
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new ForbiddenException(fieldName + " должен быть положительным числом");
        }
    }

    private void validatePagination(int from, int size) {
        if (from < 0) {
            throw new ForbiddenException("Параметр 'from' должен быть не меньше 0");
        }
        if (size <= 0 || size > 100) {
            throw new ForbiddenException("Параметр 'size' должен быть от 1 до 100");
        }
    }

    public CommentStatsDto getUserCommentStats(Long userId) {
        validateId(userId, "ID пользователя");

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        long total = commentRepository.findByAuthorId(userId, Pageable.unpaged()).size();
        long published = commentRepository.findByAuthorId(userId, Pageable.unpaged())
                .stream()
                .filter(c -> c.getStatus() == CommentStatus.PUBLISHED)
                .count();
        long pending = commentRepository.findByAuthorId(userId, Pageable.unpaged())
                .stream()
                .filter(c -> c.getStatus() == CommentStatus.PENDING)
                .count();
        long rejected = commentRepository.findByAuthorId(userId, Pageable.unpaged())
                .stream()
                .filter(c -> c.getStatus() == CommentStatus.REJECTED)
                .count();

        return new CommentStatsDto(total, published, pending, rejected);
    }

    public static class CommentStatsDto {
        private final Long total;
        private final Long published;
        private final Long pending;
        private final Long rejected;

        public CommentStatsDto(Long total, Long published, Long pending, Long rejected) {
            this.total = total;
            this.published = published;
            this.pending = pending;
            this.rejected = rejected;
        }


        public Long getTotal() { return total; }
        public Long getPublished() { return published; }
        public Long getPending() { return pending; }
        public Long getRejected() { return rejected; }
    }
}
