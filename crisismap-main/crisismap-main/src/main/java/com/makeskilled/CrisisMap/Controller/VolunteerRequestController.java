package com.makeskilled.CrisisMap.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import com.makeskilled.CrisisMap.Repository.ServiceRequestRepository;
import com.makeskilled.CrisisMap.Entity.ServiceRequest;

import java.util.HashSet;
import java.util.List;

@Controller
public class VolunteerRequestController {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @GetMapping("/volunteerServiceRequests")
    public String viewServiceRequests(Model model, HttpSession session) {
        String volunteerId = (String) session.getAttribute("username");
        if (volunteerId == null) {
            return "redirect:/userSignin";
        }

        // Get volunteer's accepted requests
        List<ServiceRequest> acceptedRequests = serviceRequestRepository.findByVolunteersContaining(volunteerId);
        
        // Get available requests (not accepted by this volunteer and not full)
        List<ServiceRequest> availableRequests = serviceRequestRepository.findAvailableRequests(volunteerId);

        // Check if volunteer has any active requests
        boolean hasActiveRequest = acceptedRequests.stream()
                .anyMatch(req -> !req.getStatus().equals("COMPLETED"));

        model.addAttribute("acceptedRequests", acceptedRequests);
        model.addAttribute("availableRequests", availableRequests);
        model.addAttribute("hasActiveRequest", hasActiveRequest);
        model.addAttribute("currentUser", volunteerId);

        return "volunteerServiceRequests";
    }

    @PostMapping("/acceptRequest")
    public String acceptServiceRequest(@RequestParam Long requestId, 
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        String volunteerId = (String) session.getAttribute("username");
        if (volunteerId == null) {
            return "redirect:/userSignin";
        }

        // Check if volunteer already has an active request
        List<ServiceRequest> activeRequests = serviceRequestRepository.findByVolunteersContaining(volunteerId);
        if (activeRequests.stream().anyMatch(req -> !req.getStatus().equals("COMPLETED"))) {
            redirectAttributes.addFlashAttribute("error", "You already have an active service request!");
            return "redirect:/volunteerServiceRequests";
        }

        ServiceRequest request = serviceRequestRepository.findById(requestId).orElse(null);
        if (request != null) {
            // Check if request is still available
            if (request.getVolunteersAccepted() >= request.getVolunteersNeeded()) {
                redirectAttributes.addFlashAttribute("error", "This request already has enough volunteers!");
                return "redirect:/volunteerServiceRequests";
            }

            // Add volunteer to request
            request.getVolunteers().add(volunteerId);
            request.setVolunteersAccepted(request.getVolunteersAccepted() + 1);
            
            // Set status to ACCEPTED if not already set
           
            serviceRequestRepository.save(request);
            redirectAttributes.addFlashAttribute("message", "Service request accepted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Service request not found!");
        }

        return "redirect:/volunteerServiceRequests";
    }

    @PostMapping("/updateRequestStatus")
    public String updateRequestStatus(@RequestParam Long requestId,
                                    @RequestParam String status,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        String volunteerId = (String) session.getAttribute("username");
        if (volunteerId == null) {
            return "redirect:/userSignin";
        }

        ServiceRequest request = serviceRequestRepository.findById(requestId).orElse(null);
        if (request != null && request.getVolunteers().contains(volunteerId)) {
            // Ensure volunteerStatuses is initialized
            if (request.getVolunteerStatuses() == null) {
                request.setVolunteerStatuses(new HashSet<>());
            }

            // Check if volunteer has already updated status twice
            boolean hasStarted = request.getVolunteerStatuses().stream()
                .anyMatch(s -> s.equals(volunteerId + "_START"));
            boolean hasCompleted = request.getVolunteerStatuses().stream()
                .anyMatch(s -> s.equals(volunteerId + "_COMPLETE"));

            if (hasStarted && status.equals("START_PROGRESS")) {
                redirectAttributes.addFlashAttribute("error", "You have already started this request!");
                return "redirect:/volunteerServiceRequests";
            }

            if (hasCompleted && status.equals("MARK_COMPLETE")) {
                redirectAttributes.addFlashAttribute("error", "You have already marked this request as complete!");
                return "redirect:/volunteerServiceRequests";
            }

            // Logic for START_PROGRESS
            if (status.equals("START_PROGRESS")) {
                request.setVstart(request.getVstart() + 1);
                request.getVolunteerStatuses().add(volunteerId + "_START");
                
                // Change status to IN_PROGRESS if all volunteers have started
                if (request.getVstart() == request.getVolunteersNeeded()) {
                    request.setStatus("IN_PROGRESS");
                }
            }

            // Logic for MARK_COMPLETE
            if (status.equals("MARK_COMPLETE")) {
                request.setVcompleted(request.getVcompleted() + 1);
                request.getVolunteerStatuses().add(volunteerId + "_COMPLETE");
                
                // Change status to COMPLETED if all volunteers have marked complete
                if (request.getVcompleted() == request.getVolunteersNeeded()) {
                    request.setStatus("COMPLETED");
                }
            }

            serviceRequestRepository.save(request);
            redirectAttributes.addFlashAttribute("message", "Status updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Service request not found or unauthorized!");
        }

        return "redirect:/volunteerServiceRequests";
    }
    @PostMapping("/withdrawRequest")
    public String withdrawFromRequest(@RequestParam Long requestId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        String volunteerId = (String) session.getAttribute("username");
        if (volunteerId == null) {
            return "redirect:/userSignin";
        }

        ServiceRequest request = serviceRequestRepository.findById(requestId).orElse(null);
        if (request != null && request.getVolunteers().contains(volunteerId)) {
            // Only allow withdrawal when status is PENDING
            if (request.getStatus().equals("PENDING")) {
                request.getVolunteers().remove(volunteerId);
                request.setVolunteersAccepted(request.getVolunteersAccepted() - 1);
                
                serviceRequestRepository.save(request);
                redirectAttributes.addFlashAttribute("message", "Withdrawn from service request successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "You can only withdraw from pending requests!");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Service request not found or unauthorized!");
        }

        return "redirect:/volunteerServiceRequests";}
    
}