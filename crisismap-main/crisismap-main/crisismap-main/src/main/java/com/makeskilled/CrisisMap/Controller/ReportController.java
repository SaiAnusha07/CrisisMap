package com.makeskilled.CrisisMap.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.makeskilled.CrisisMap.Entity.Report;
import com.makeskilled.CrisisMap.Repository.ReportRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @GetMapping("/createReport")
    public String createReport() {
        return "createReport";
    }

    @GetMapping("/myreports")
    public String myReports(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");
        List<Report> userReports = reportRepository.findBySubmittedBy(loggedInUser);
        model.addAttribute("reports", userReports);
        return "myreports";
    }

    @GetMapping("/ngoReports")
    public String myReports1(HttpSession session, Model model) {
        List<Report> userReports = reportRepository.findAll();
        model.addAttribute("reports", userReports);
        return "ngoReports";
    }



    @PostMapping("/submit-report")
    public String submitReport(@RequestParam String title,
                            @RequestParam String location,
                            @RequestParam String description,
                            @RequestParam String date,
                            HttpSession session) {
        Report report = new Report();
        report.setTitle(title);
        report.setLocation(location);
        report.setDescription(description);
        report.setDate(LocalDate.parse(date));

        String loggedInUser = (String) session.getAttribute("username");
        report.setSubmittedBy(loggedInUser);
        report.setStatus("PENDING");
        report.setRemarks("awaiting response");

        reportRepository.save(report);
        return "redirect:/myreports";
    }
    @PostMapping("/ngoReports/update")
    public String updateReportStatus(
            @RequestParam Long reportId, 
            @RequestParam String status, 
            HttpSession session) {
        
        // Get the currently logged-in user
        String currentUser = (String) session.getAttribute("username");
        if (currentUser == null) {
            // Redirect with an error message if no user is logged in
            return "redirect:/ngoReports?error=unauthorized";
        }

        // Find the report
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Report ID: " + reportId));

        // Check if the status transition is valid
        if (!report.canTransitionTo(status)) {
            // Redirect with an error message for invalid status transition
            return "redirect:/ngoReports?error=invalidTransition";
        }

        // Check if the report is already accepted by another user
        if (report.getAccepted() != null && !report.getAccepted().equals(currentUser)) {
            // Redirect with an error message if another user has already accepted the report
            return "redirect:/ngoReports?error=alreadyAccepted";
        }

        try {
            // Update status and set the user who accepted/changed the status
        
        	
        	report.setAccepted(currentUser);
        	report.updateStatus(status);
        	
            
            Report savedReport = reportRepository.save(report);
            System.out.println("Report updated successfully. New status: " + savedReport.getStatus() + 
                               ", Accepted by: " + savedReport.getAccepted());
        } catch (Exception e) {
            System.err.println("Error updating report status: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/ngoReports?error=updateFailed";
        }

        return "redirect:/ngoReports";}
        

    @PostMapping("/ngoReports/updateRemarks")
    public String updateReportRemarks(@RequestParam Long reportId, @RequestParam String remarks) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Report ID: " + reportId));

        report.setRemarks(remarks);
        reportRepository.save(report);

        return "redirect:/ngoReports"; // Redirect back to the reports page
    }
    
}
