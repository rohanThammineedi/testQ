package qfTest.QF.Test.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qfTest.QF.Test.service.JiraIssueService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    
    private final JiraIssueService jiraIssueService;

    @PostMapping("/verify")
    public boolean verifySync() throws Exception{
        return jiraIssueService.verifySync();
    }
}
