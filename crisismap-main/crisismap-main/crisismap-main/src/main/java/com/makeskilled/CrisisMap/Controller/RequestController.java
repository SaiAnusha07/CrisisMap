package com.makeskilled.CrisisMap.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.makeskilled.CrisisMap.Entity.Request;
import com.makeskilled.CrisisMap.Entity.Status;
import com.makeskilled.CrisisMap.Repository.RequestRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    @GetMapping("/makeRequest")
    public String makeRequest() {
        return "makeRequest";
    }
    

    @PostMapping("/submit-request")
    public String submitRequest(@RequestParam String resourceName,
                                @RequestParam int quantity,
                                @RequestParam String location,
                                @RequestParam String description,
                                HttpSession session) {
        Request request = new Request();
        request.setResourceName(resourceName);
        request.setQuantity(quantity);
        request.setLocation(location);
        request.setDescription(description);

        String loggedInUser = (String) session.getAttribute("username");
        request.setSubmittedBy(loggedInUser);

        requestRepository.save(request);
        return "redirect:/myRequests";
    }

    @GetMapping("/myRequests")
    public String myRequests(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");
        List<Request> requests = requestRepository.findBySubmittedBy(loggedInUser);
        model.addAttribute("requests", requests);
        return "myRequests";
    }

    @GetMapping("/ngoRequests")
    public String ngoRequestsPage(Model model){
        List<Request> requests = requestRepository.findAll();
        model.addAttribute("requests", requests);
        return "ngoRequests";
    }
    @PostMapping("/requests/update-status-ngo")
    public String updateTaskStatusByNGO(@RequestParam Long taskId, 
                                        @RequestParam Status status, 
                                        HttpSession session) {
        String currentUser = (String) session.getAttribute("username");
        Request request = requestRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Request ID: " + taskId));

        // Special case: Allow PENDING to IN_PROGRESS transition by any user
        if (request.getStatus() == Status.PENDING && status == Status.IN_PROGRESS) {
            request.setAcceptedUser(currentUser);
        } 
        // For other transitions, only acceptedUser can change status
        else if (request.getAcceptedUser() == null || !request.getAcceptedUser().equals(currentUser)) {
            return "redirect:/ngoRequests?error=unauthorized";
        }

        // Validate status transitions
        boolean isValidTransition = isValidStatusTransition(request.getStatus(), status);
        if (!isValidTransition) {
            return "redirect:/ngoRequests?error=invalidTransition";
        }

        // Handle acceptedUser based on status transition
        if (request.getStatus() == Status.IN_PROGRESS && status == Status.PENDING) {
            request.setAcceptedUser(null);
        }

        request.setStatus(status);
        requestRepository.save(request);

        return "redirect:/ngoRequests";
    }

    // Helper method to validate status transitions
    private boolean isValidStatusTransition(Status currentStatus, Status newStatus) {
        switch (currentStatus) {
            case PENDING:
                return newStatus == Status.IN_PROGRESS || newStatus == Status.COMPLETED;
            case IN_PROGRESS:
                return newStatus == Status.PENDING || newStatus == Status.COMPLETED;
            default:
                return false;
        }
    }

  
}
