package com.sportradar.sportevents.controller.view;

import com.sportradar.sportevents.service.SportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sports")
@RequiredArgsConstructor
public class SportViewController {

    private final SportService sportService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sports", sportService.findAll());
        return "sports/list";
    }
}
