package com.makeskilled.CrisisMap.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.makeskilled.CrisisMap.Entity.Report;
import com.makeskilled.CrisisMap.Entity.Status;
import com.makeskilled.CrisisMap.Repository.ReportRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class VolunteerReportController {

    @Autowired
    private ReportRepository reportRepository;

    @GetMapping("/volunteerReports")
    public String viewReports(Model model, HttpSession session) {
        String volunteerId = (String) session.getAttribute("username");
        if (volunteerId == null) {
            return "redirect:/userSignin";
        }
        model.addAttribute("user", volunteerId);
        List<Report> reports = reportRepository.findAll();
        model.addAttribute("reports", reports);
        model.addAttribute("statuses", Status.values());
        return "volunteerReports";
    }

    @PostMapping("/volunteerReports/update")
    public String updateReportStatus(@RequestParam Long reportId, @RequestParam Status status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Report ID: " + reportId));
        report.setStatus(status.toString());
        reportRepository.save(report);
        return "redirect:/volunteerReports";
    }
}
