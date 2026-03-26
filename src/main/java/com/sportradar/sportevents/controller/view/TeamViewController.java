package com.sportradar.sportevents.controller.view;

import com.sportradar.sportevents.service.SportService;
import com.sportradar.sportevents.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamViewController {

    private final TeamService teamService;
    private final SportService sportService;

    @GetMapping
    public String list(Model model, @RequestParam(required = false) Long sportId) {
        var teams = (sportId != null) ? teamService.findBySport(sportId) : teamService.findAll();
        model.addAttribute("teams", teams);
        model.addAttribute("sports", sportService.findAll());
        model.addAttribute("selectedSportId", sportId);
        return "teams/list";
    }
}
