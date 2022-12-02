package ru.ssau.volunteerapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.volunteerapi.model.dto.response.ApplicationResponse;
import ru.ssau.volunteerapi.model.dto.general.EventGeneral;
import ru.ssau.volunteerapi.service.interfaces.EventService;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final EventService eventService;

    @PostMapping("/createEvent")
    public ResponseEntity<EventGeneral> createEvent(@RequestBody EventGeneral eventGeneral) {
        return ResponseEntity.ok(eventService.createEvent(eventGeneral));
    }

    @DeleteMapping("/deleteEvent/{event_id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("event_id") Integer id) {
        return ResponseEntity.ok(eventService.deleteEvent(id));
    }

    @GetMapping("/{event_id}/applications")
    public ResponseEntity<ApplicationResponse> getApplicationsByEventId(@PathVariable("event_id") Integer id) {
        return ResponseEntity.ok(eventService.getApplicationsByEventId(id));
    }

    @GetMapping("/{event_id}/applications/{user_id}")
    public ResponseEntity<Void> changeUserStatusInApplication(@PathVariable("event_id") Integer id,
                                                                       @PathVariable("user_id")UUID userId) {
        return ResponseEntity.ok(eventService.changeUserStatusInApplication(id,userId));
    }
}
