package com.sportradar.sportevents.controller.view;

import com.sportradar.sportevents.domain.EventStatus;
import com.sportradar.sportevents.dto.response.EventResponse;
import com.sportradar.sportevents.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final EventService eventService;

    @GetMapping("/")
    public String dashboard(Model model) {
        List<EventResponse> events = eventService.findAll();

        List<EventResponse> sorted = events.stream()
                .sorted(Comparator.comparingInt(e -> switch (e.status()) {
                    case LIVE -> 0;
                    case SCHEDULED -> 1;
                    case FINISHED -> 2;
                }))
                .toList();

        model.addAttribute("events", sorted);
        model.addAttribute("liveCount", events.stream().filter(e -> e.status() == EventStatus.LIVE).count());
        model.addAttribute("scheduledCount", events.stream().filter(e -> e.status() == EventStatus.SCHEDULED).count());
        model.addAttribute("finishedCount", events.stream().filter(e -> e.status() == EventStatus.FINISHED).count());
        model.addAttribute("totalCount", events.size());
        return "index";
    }
}
