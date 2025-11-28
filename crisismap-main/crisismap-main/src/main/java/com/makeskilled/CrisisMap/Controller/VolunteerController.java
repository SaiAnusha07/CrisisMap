package com.makeskilled.CrisisMap.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class VolunteerController {

    @GetMapping("/vDashboard")
    public String showDashboard(Model model, HttpSession session) {
        String volunteerId = (String) session.getAttribute("username");
        if (volunteerId == null) {
            return "redirect:/userSignin";
        }
        model.addAttribute("user", volunteerId);
        return "vDashboard";
    }
}
