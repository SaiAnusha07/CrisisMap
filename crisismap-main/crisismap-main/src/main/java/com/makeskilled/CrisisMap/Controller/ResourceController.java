package com.makeskilled.CrisisMap.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.makeskilled.CrisisMap.Entity.NgoResourceUsage;
import com.makeskilled.CrisisMap.Entity.Resource;
import com.makeskilled.CrisisMap.Repository.NgoResourceUsageRepository;
import com.makeskilled.CrisisMap.Repository.ResourceRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    @PostMapping("/resources/add")
    public String addResource(@RequestParam String resourceName,
                              @RequestParam int quantity,
                              @RequestParam String location,
                              @RequestParam String mobileNo,
                              @RequestParam(required = false) String description) {
        // Create and save resource
        Resource resource = new Resource();
        resource.setResourceName(resourceName);
        resource.setQuantity(quantity);
        resource.setLocation(location);
        resource.setMobileNo(mobileNo);
        resource.setDescription(description);

        resourceRepository.save(resource);

        return "redirect:/resources"; // Redirect to a resources list or dashboard
    }

    @GetMapping("/addResource")
    public String addResourcePage() {
        return "addResource";
    }

    @GetMapping("/resources")
    public String viewResources(Model model) {
        List<Resource> resources = resourceRepository.findAll();
        model.addAttribute("resources", resources);
        return "viewResources"; // Create a resourceList.html file to display resources
    }

    @GetMapping("/ngoResources")
    public String viewResources1(Model model) {
        List<Resource> resources = resourceRepository.findAll();
        model.addAttribute("resources", resources);
        return "ngoResources"; // Create a resourceList.html file to display resources
    }

    @GetMapping("/resources/edit/{id}")
    public String editResource(@PathVariable Long id, Model model) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resource ID: " + id));
        model.addAttribute("resource", resource);
        return "editResource"; // Display the editResource.html form
    }

    @PostMapping("/resources/update")
    public String updateResource(@RequestParam Long id,
                                 @RequestParam String resourceName,
                                 @RequestParam int quantity,
                                 @RequestParam String location,
                                 @RequestParam(required = false) String description) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resource ID: " + id));

        resource.setResourceName(resourceName);
        resource.setQuantity(quantity);
        resource.setLocation(location);
        resource.setDescription(description);

        resourceRepository.save(resource);
        return "redirect:/resources"; // Redirect back to the resource list
    }

    @GetMapping("/resources/edit1/{id}")
    public String editResource1(@PathVariable Long id, Model model) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resource ID: " + id));
        model.addAttribute("resource", resource);
        return "ngoEditResource"; // Display the editResource.html form
    }

    @PostMapping("/resources/update1")
    public String updateResource1(@RequestParam Long id,
                                 @RequestParam String resourceName,
                                 @RequestParam int quantity,
                                 @RequestParam String location,
                                 @RequestParam(required = false) String description) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid resource ID: " + id));

        resource.setResourceName(resourceName);
        resource.setQuantity(quantity);
        resource.setLocation(location);
        resource.setDescription(description);

        resourceRepository.save(resource);
        return "redirect:/ngoResources"; // Redirect back to the resource list
    }

    // Delete Resource
    @GetMapping("/resources/delete/{id}")
    public String deleteResource(@PathVariable Long id) {
        resourceRepository.deleteById(id);
        return "redirect:/resources"; // Redirect back to the resource list
    }
    
    @Autowired
    private NgoResourceUsageRepository ngoResourceUsageRepository;

    @GetMapping("/ngo/request-resource/{resourceId}")
    public String requestResourcePage(@PathVariable Long resourceId, Model model, HttpSession session) {
        String ngoUsername = (String) session.getAttribute("username");
        if (ngoUsername == null) {
            return "redirect:/userSignin";
        }

        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new RuntimeException("Resource not found"));
        
        model.addAttribute("resource", resource);
        return "ngoRequestResource";
    }

    @PostMapping("/ngo/use-resource")
    public String useResource(
        @RequestParam Long resourceId, 
        @RequestParam int quantity, 
        HttpSession session, 
        RedirectAttributes redirectAttributes
    ) {
        // Get the current logged-in NGO username from session
        String ngoUsername = (String) session.getAttribute("username");
        
        if (ngoUsername == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in first");
            return "redirect:/userSignin";
        }

        // Find the resource
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new RuntimeException("Resource not found"));

        // Check if sufficient quantity is available
        if (resource.getQuantity() < quantity) {
            redirectAttributes.addFlashAttribute("error", "Insufficient resource quantity");
            return "redirect:/ngoResources";
        }

        // Reduce resource quantity
        resource.setQuantity(resource.getQuantity() - quantity);
        resourceRepository.save(resource);

        // Create and save NGO resource usage record
        NgoResourceUsage usage = new NgoResourceUsage(resourceId, ngoUsername, quantity);
        ngoResourceUsageRepository.save(usage);

        redirectAttributes.addFlashAttribute("success", "Resource usage recorded successfully");
        return "redirect:/ngoResources";
    }
    
    @GetMapping("/ngo/resource-usage/{resourceId}")
    public String viewResourceUsageDetails(
        @PathVariable Long resourceId, 
        Model model
    ) {
        // Find the resource
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new RuntimeException("Resource not found"));
        
        // Find all usage records for this resource
        List<NgoResourceUsage> resourceUsages = ngoResourceUsageRepository
            .findAllByResourceId(resourceId);
        
        model.addAttribute("resource", resource);
        model.addAttribute("resourceUsages", resourceUsages);
        
        return "ngoResourceUsageDetails";
    }
}
