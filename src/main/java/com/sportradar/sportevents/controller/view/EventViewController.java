package com.sportradar.sportevents.controller.view;

import com.sportradar.sportevents.domain.EventStatus;
import com.sportradar.sportevents.dto.request.EventRequest;
import com.sportradar.sportevents.dto.request.EventTeamRequest;
import com.sportradar.sportevents.dto.request.UpdateScoreRequest;
import com.sportradar.sportevents.dto.request.UpdateStatusRequest;
import com.sportradar.sportevents.service.EventService;
import com.sportradar.sportevents.service.SportService;
import com.sportradar.sportevents.service.TeamService;
import com.sportradar.sportevents.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventViewController {

    private final EventService eventService;
    private final SportService sportService;
    private final VenueService venueService;
    private final TeamService teamService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) EventStatus status,
                       @RequestParam(required = false) Long sportId,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var events = (status != null && sportId != null) ? eventService.findByStatus(status).stream()
                        .filter(e -> e.sportId().equals(sportId)).toList()
                   : (status != null)             ? eventService.findByStatus(status)
                   : (sportId != null)            ? eventService.findBySport(sportId)
                   : (from != null && to != null) ? eventService.findByDateRange(from, to)
                   : eventService.findAll();

        model.addAttribute("events", events);
        model.addAttribute("sports", sportService.findAll());
        model.addAttribute("statuses", EventStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedSportId", sportId);
        model.addAttribute("selectedFrom", from);
        model.addAttribute("selectedTo", to);
        return "events/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.findById(id));
        model.addAttribute("statuses", EventStatus.values());
        return "events/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("sports", sportService.findAll());
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("teams", teamService.findAll());
        model.addAttribute("statuses", EventStatus.values());
        return "events/form";
    }

    @PostMapping("/new")
    public String createEvent(
            @RequestParam String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime eventTime,
            @RequestParam Long sportId,
            @RequestParam Long venueId,
            @RequestParam(defaultValue = "SCHEDULED") EventStatus status,
            @RequestParam(required = false) Long homeTeamId,
            @RequestParam(required = false) Long awayTeamId,
            RedirectAttributes redirectAttributes) {
        try {
            EventRequest request = new EventRequest(title, eventDate, eventTime, status, sportId, venueId);
            var created = eventService.create(request);
            Long eventId = created.id();

            if (homeTeamId != null) {
                eventService.addTeam(eventId, new EventTeamRequest(homeTeamId, true, null));
            }
            if (awayTeamId != null && !awayTeamId.equals(homeTeamId)) {
                eventService.addTeam(eventId, new EventTeamRequest(awayTeamId, false, null));
            }

            redirectAttributes.addFlashAttribute("successMessage", "Event \"" + title + "\" created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create event: " + e.getMessage());
            return "redirect:/events/new";
        }
        return "redirect:/events";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam EventStatus status,
                               RedirectAttributes redirectAttributes) {
        try {
            eventService.updateStatus(id, new UpdateStatusRequest(status));
            redirectAttributes.addFlashAttribute("successMessage", "Status updated to " + status.name() + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/teams/{teamId}/score")
    public String updateScore(@PathVariable Long id,
                              @PathVariable Long teamId,
                              @RequestParam Integer score,
                              RedirectAttributes redirectAttributes) {
        try {
            eventService.updateScore(id, teamId, new UpdateScoreRequest(score));
            redirectAttributes.addFlashAttribute("successMessage", "Score updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update score: " + e.getMessage());
        }
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete event: " + e.getMessage());
        }
        return "redirect:/events";
    }
}
