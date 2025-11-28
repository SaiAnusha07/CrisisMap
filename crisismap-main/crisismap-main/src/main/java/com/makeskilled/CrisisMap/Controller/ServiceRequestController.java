package com.makeskilled.CrisisMap.Controller;

import com.makeskilled.CrisisMap.Entity.ServiceRequest;
import com.makeskilled.CrisisMap.Repository.ServiceRequestRepository;
import com.makeskilled.CrisisMap.Repository.VolunteerRepository;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ngo")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    // NGO endpoints
    private static final Logger logger = LoggerFactory.getLogger(ServiceRequestController.class);

    @GetMapping("/serviceRequests")
    public String showServiceRequests(Model model, HttpSession session) {
        String ngoId = (String) session.getAttribute("username");
        if (ngoId == null) {
            logger.warn("Unauthorized access attempt to service requests");
            return "redirect:/userSignin";
        }

        try {
            List<ServiceRequest> requests = serviceRequestRepository.findByRequestedBy(ngoId);
            
            if (requests.isEmpty()) {
                logger.info("No service requests found for NGO: {}", ngoId);
                model.addAttribute("message", "No service requests available.");
            } else {
                model.addAttribute("serviceRequests", requests);
                logger.debug("Retrieved {} service requests for NGO: {}", requests.size(), ngoId);
            }
        } catch (Exception e) {
            logger.error("Error retrieving service requests for NGO: {}", ngoId, e);
            model.addAttribute("error", "Unable to retrieve service requests. Please try again.");
        }
        
        return "createServiceRequest";
    }

    @PostMapping("/serviceRequests")
    public String createServiceRequest(@ModelAttribute ServiceRequest serviceRequest,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        String ngoId = (String) session.getAttribute("username");
        if (ngoId == null) {
            return "redirect:/userSignin";
        }

        serviceRequest.setRequestedBy(ngoId);
        serviceRequest.setRequestDateTime(LocalDateTime.now());
        serviceRequest.setStatus("PENDING");
        serviceRequest.setVolunteersAccepted(0);
        serviceRequestRepository.save(serviceRequest);
        
        redirectAttributes.addFlashAttribute("message", "Service request created successfully!");
        return "redirect:/ngo/serviceRequests";
    }

    @PostMapping("/serviceRequests/{id}/cancel")
    public String cancelServiceRequest(@PathVariable Long id, 
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        String ngoId = (String) session.getAttribute("username");
        if (ngoId == null) {
            return "redirect:/userSignin";
        }

        ServiceRequest request = serviceRequestRepository.findById(id).orElse(null);
        if (request != null && request.getRequestedBy().equals(ngoId)) {
            request.setStatus("CANCELLED");
            serviceRequestRepository.save(request);
            redirectAttributes.addFlashAttribute("message", "Service request cancelled successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Service request not found or unauthorized!");
        }
        return "redirect:/ngo/serviceRequests";
    }

    @PostMapping("/serviceRequests/{id}/updateStatus")
    public String updateServiceRequestStatus(@PathVariable Long id,
                                           @RequestParam String status,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        String ngoId = (String) session.getAttribute("username");
        if (ngoId == null) {
            return "redirect:/userSignin";
        }

        ServiceRequest request = serviceRequestRepository.findById(id).orElse(null);
        if (request != null && request.getRequestedBy().equals(ngoId)) {
            if (isValidStatusTransition(request.getStatus(), status)) {
                request.setStatus(status);
                serviceRequestRepository.save(request);
                redirectAttributes.addFlashAttribute("message", "Service request status updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid status transition!");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Service request not found or unauthorized!");
        }

        return "redirect:/ngo/serviceRequests";
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        switch (currentStatus) {
            case "PENDING":
                return newStatus.equals("IN_PROGRESS") || newStatus.equals("CANCELLED");
            case "IN_PROGRESS":
                return newStatus.equals("COMPLETED") || newStatus.equals("CANCELLED");
            case "COMPLETED":
                return false; // Cannot change status once completed
            case "CANCELLED":
                return false; // Cannot change status once cancelled
            default:
                return false;
        }
    }

    @GetMapping("/serviceStats")
    public String viewServiceRequestStats(Model model, HttpSession session) {
        String ngoId = (String) session.getAttribute("username");
        if (ngoId == null) {
            return "redirect:/userSignin";
        }

        // Get NGO-specific requests
        List<ServiceRequest> ngoRequests = serviceRequestRepository.findActiveRequestsByNgo(ngoId);
        
        // Calculate statistics for NGO's requests
        long totalRequests = ngoRequests.size();
        long pendingRequests = ngoRequests.stream()
            .filter(req -> req.getStatus().equals("PENDING"))
            .count();
        long acceptedRequests = ngoRequests.stream()
            .filter(req -> req.getStatus().equals("ACCEPTED") || req.getStatus().equals("IN_PROGRESS"))
            .count();
        long completedRequests = ngoRequests.stream()
            .filter(req -> req.getStatus().equals("COMPLETED"))
            .count();

        // Add statistics to model
        model.addAttribute("totalRequests", totalRequests);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("acceptedRequests", acceptedRequests);
        model.addAttribute("completedRequests", completedRequests);
        model.addAttribute("requests", ngoRequests);

        return "serviceRequestStats";
    }

    // Volunteer endpoints
    @GetMapping("/volunteer/serviceRequests")
    public String showVolunteerRequests(Model model, HttpSession session) {
        String volunteerId = (String) session.getAttribute("username");
        if (volunteerId == null) {
            return "redirect:/userSignin";
        }

        // Get volunteer's accepted requests
        List<ServiceRequest> acceptedRequests = serviceRequestRepository.findByVolunteersContaining(volunteerId);
        
        // Check if volunteer has any active requests
        boolean hasActiveRequest = acceptedRequests.stream()
                .anyMatch(req -> !req.getStatus().equals("COMPLETED"));

        // Get available requests
        List<ServiceRequest> availableRequests = hasActiveRequest ? 
            List.of() : serviceRequestRepository.findAvailableRequests(volunteerId);

        model.addAttribute("acceptedRequests", acceptedRequests);
        model.addAttribute("availableRequests", availableRequests);
        model.addAttribute("hasActiveRequest", hasActiveRequest);
        
        return "volunteerServiceRequests";
    }

    @PostMapping("/volunteer/serviceRequests/{id}/accept")
    public String acceptServiceRequest(@PathVariable Long id, 
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
            return "redirect:/volunteer/serviceRequests";
        }

        ServiceRequest request = serviceRequestRepository.findById(id).orElse(null);
        if (request != null) {
            // Check if request is still available
            if (request.getVolunteersAccepted() >= request.getVolunteersNeeded()) {
                redirectAttributes.addFlashAttribute("error", "This request already has enough volunteers!");
                return "redirect:/volunteer/serviceRequests";
            }

            // Add volunteer to request WITHOUT changing status
            request.getVolunteers().add(volunteerId);
            request.setVolunteersAccepted(request.getVolunteersAccepted() + 1);
            serviceRequestRepository.save(request);
            redirectAttributes.addFlashAttribute("message", "Service request accepted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Service request not found!");
        }

        return "redirect:/volunteer/serviceRequests";
    }
    @PostMapping({"/serviceRequests/{id}/updateVolunteers", "/ngo/serviceRequests/{id}/updateVolunteers"})
    public String updateVolunteersNeeded(@PathVariable Long id, 
                                       @RequestParam int volunteersNeeded,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        // Add detailed logging
        logger.info("Attempting to update volunteers for request ID: {}", id);
        logger.info("Volunteers needed: {}", volunteersNeeded);

        String userId = (String) session.getAttribute("username");
        if (userId == null) {
            logger.warn("Unauthorized access: No user in session");
            return "redirect:/userSignin";
        }

        ServiceRequest request = serviceRequestRepository.findById(id).orElse(null);
        if (request == null) {
            logger.error("Service request not found with ID: {}", id);
            redirectAttributes.addFlashAttribute("error", "Service request not found!");
            return "redirect:/ngo/serviceRequests";
        }

        // Ensure the request belongs to the current user
        if (!request.getRequestedBy().equals(userId)) {
            logger.warn("Unauthorized modification attempt by user: {}", userId);
            redirectAttributes.addFlashAttribute("error", "Unauthorized to modify this request!");
            return "redirect:/ngo/serviceRequests";
        }

        // Validate volunteers needed
        if (volunteersNeeded < request.getVolunteersAccepted()) {
            logger.warn("Invalid volunteers needed: {} < {}", 
                volunteersNeeded, request.getVolunteersAccepted());
            redirectAttributes.addFlashAttribute("error", 
                "Number of volunteers needed cannot be less than currently accepted volunteers (" 
                + request.getVolunteersAccepted() + ")");
            return "redirect:/ngo/serviceRequests";
        }

        // Update volunteers needed
        request.setVolunteersNeeded(volunteersNeeded);
        serviceRequestRepository.save(request);
        
        logger.info("Successfully updated volunteers needed to {} for request ID: {}", 
            volunteersNeeded, id);
        redirectAttributes.addFlashAttribute("message", "Number of volunteers updated successfully!");
        return "redirect:/ngo/serviceRequests";
    }
    @PostMapping("/volunteer/serviceRequests/{id}/withdraw")
    public String withdrawFromService(@PathVariable Long id, 
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        String volunteerId = (String) session.getAttribute("username");
        if (volunteerId == null) {
            return "redirect:/userSignin";
        }

        ServiceRequest request = serviceRequestRepository.findById(id).orElse(null);
        if (request != null && request.getVolunteers().contains(volunteerId)) {
            if (!request.getStatus().equals("COMPLETED")) {
                request.getVolunteers().remove(volunteerId);
                request.setVolunteersAccepted(request.getVolunteersAccepted() - 1);
                if (request.getVolunteersAccepted() == 0) {
                    request.setStatus("PENDING");
                }
                serviceRequestRepository.save(request);
                redirectAttributes.addFlashAttribute("message", "Withdrawn from service request successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Cannot withdraw from completed request!");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Service request not found or unauthorized!");
        }

        return "redirect:/volunteer/serviceRequests";
    }
}
