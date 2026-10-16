package com.vaultiq.sentinel.controller;

import com.vaultiq.sentinel.dto.RedTeamPrompt;
import com.vaultiq.sentinel.dto.RedTeamResult;
import com.vaultiq.sentinel.service.RedTeamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sentinel/redteam")
public class RedTeamController {

    private final RedTeamService redTeamService;

    public RedTeamController(RedTeamService redTeamService) {
        this.redTeamService = redTeamService;
    }

    @GetMapping
    public List<RedTeamPrompt> prompts() {
        return redTeamService.prompts();
    }

    @PostMapping("/run")
    public List<RedTeamResult> run() {
        return redTeamService.run();
    }
}
