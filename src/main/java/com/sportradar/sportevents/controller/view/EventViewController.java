package com.sportradar.sportevents.controller.view;

import com.sportradar.sportevents.domain.EventStatus;
import com.sportradar.sportevents.service.EventService;
import com.sportradar.sportevents.service.SportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventViewController {

    private final EventService eventService;
    private final SportService sportService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) EventStatus status,
                       @RequestParam(required = false) Long sportId) {
        var events = (status != null) ? eventService.findByStatus(status)
                   : (sportId != null) ? eventService.findBySport(sportId)
                   : eventService.findAll();

        model.addAttribute("events", events);
        model.addAttribute("sports", sportService.findAll());
        model.addAttribute("statuses", EventStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedSportId", sportId);
        return "events/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventService.findById(id));
        return "events/detail";
    }
}
