package ru.ssau.volunteerapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ssau.volunteerapi.exception.NotFoundException;
import ru.ssau.volunteerapi.model.dto.response.ApplicationResponse;
import ru.ssau.volunteerapi.model.entitie.*;
import ru.ssau.volunteerapi.model.mapper.ApplicationMapper;
import ru.ssau.volunteerapi.repository.ApplicationRepository;
import ru.ssau.volunteerapi.repository.UserRepository;
import ru.ssau.volunteerapi.service.interfaces.ApplicationService;
import ru.ssau.volunteerapi.service.interfaces.EventService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;
    private final EventService eventService;


    @Override
    public List<ApplicationResponse> getAllApplications() {
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByLogin(userLogin);
        List<Application> applications = applicationRepository.findByUserId(user);
        log.info("Пользователь {} просматривает все свои заявки.", userLogin);
        return applicationMapper.toResponses(applications);
    }

    @Override
    public ApplicationResponse getApplicationById(Integer id) {
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        SimpleGrantedAuthority authority = (SimpleGrantedAuthority) SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .findAny()
                .orElseThrow();
        Role role = Role.valueOf(authority.getAuthority());
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Заявка с id: " + id + " не найдена"));
        if (!Objects.equals(application.getUserId().getLogin(), userLogin) && !role.equals(Role.ADMIN)) {
            throw new AccessDeniedException("Нет прав для просмотра заявки");
        }
        log.info("Пользователь {} просматривает свою заявку с id {}.", userLogin, id);
        return applicationMapper.toResponse(application);
    }

    @Override
    public ApplicationResponse applyToEvent(Integer eventId) {
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByLogin(userLogin);
        Event event = eventService.findEntityById(eventId);
        if (applicationRepository.findByUserIdAndEventId(user, event) != null) {
            throw new IllegalArgumentException("You have already apply to this event");
        }
        Application application = Application.builder()
                .userId(user)
                .eventId(event)
                .status(ApplicationStatus.WAITED)
                .build();
        log.info("Пользователь {} подает заявку на {}.", userLogin, event.getTitle());
        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Override
    public List<ApplicationResponse> getApplicationsByEventId(Integer id) {
        String adminLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        Event event = eventService.findEntityById(id);
        log.info("Админ {} просматривает все заявки мероприятия {}.", adminLogin, event.getTitle());
        return applicationMapper.toResponses(applicationRepository.findAllByEventId(event));
    }

    @Override
    public Void changeUserStatusInApplication(Integer eventId, UUID userId, ApplicationStatus status) {
        String adminLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        Event event = eventService.findEntityById(eventId);
        Application application = applicationRepository.findAllByEventId(event)
                .stream()
                .filter(application1 -> application1.getUserId().getId().equals(userId))
                .findAny()
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не имеет заявки на мероприятие: " + event.getTitle()));
        application.setStatus(status);
        applicationRepository.save(application);
        log.info("Админ {} меняет статус заявки пользователя {} на {}.", adminLogin, userId, status);
        return null;
    }

    @Override
    public Void deleteApplication(Integer id) {
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        Application application = applicationRepository.findByEventId(eventService.findEntityById(id)).
                orElseThrow(() -> new NotFoundException("Заявки с id:" + id + " не существует"));
        if (!application.getUserId().getLogin().equals(userLogin)) {
            throw new AccessDeniedException("Нет прав для удаления заявки");
        }
        applicationRepository.delete(application);
        log.info("Пользователь {} удаляет свою заявку {}.", userLogin, id);
        return null;
    }
}
