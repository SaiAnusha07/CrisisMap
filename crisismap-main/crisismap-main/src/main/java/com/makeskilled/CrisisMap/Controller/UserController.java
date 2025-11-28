package com.makeskilled.CrisisMap.Controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.makeskilled.CrisisMap.Entity.User;
import com.makeskilled.CrisisMap.Repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public String signup(@RequestParam String username, 
                        @RequestParam String email, 
                        @RequestParam String password, 
                        @RequestParam String role, 
                        @RequestParam String location,
                        RedirectAttributes redirectAttributes) {
        // Check if email already exists
        if (userRepository.findByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("error", "Email already exists");
            return "redirect:/userSignup";
        }

        // Check if username already exists
        if (userRepository.findByUsername(username) != null) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/userSignup";
        }

        // Validate role
        List<String> validRoles = Arrays.asList("user", "volunteer", "government", "ngo","admin");
        if (!validRoles.contains(role.toLowerCase())) {
            redirectAttributes.addFlashAttribute("error", "Invalid role selected");
            return "redirect:/userSignup";
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // Ideally, hash the password before saving
        user.setRole(role.toLowerCase()); // Save role in lowercase for consistency
        user.setLocation(location);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Signup successful! Please sign in.");
        return "redirect:/userSignin";
    }

    @PostMapping("/signin")
    public String signin(@RequestParam String username, 
                        @RequestParam String password, 
                        @RequestParam String role, 
                        HttpSession session, 
                        RedirectAttributes redirectAttributes) {
        // Find user by username
        User user = userRepository.findByUsername(username);

        // Check if user exists and password matches
        if (user == null || !user.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/userSignin";
        }

        // Verify role
        if (!user.getRole().equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("error", "Invalid role for the username");
            return "redirect:/userSignin";
        }

        // Store user details in session
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        if(user.getRole().equals("user")) {
            // Redirect to the dashboard
            return "redirect:/dashboard";
        } 

        if(user.getRole().equals("volunteer")){
            return "redirect:/vdashboard";
        }

        if(user.getRole().equals("government")){
            return "redirect:/gdashboard";
        }

        if(user.getRole().equals("ngo")) {
            return "redirect:/ngodashboard";
        }

        if(user.getRole().equals("admin")) {
            return "redirect:/admindashboard";
        }

        return "redirect:/userSignin";
    }
 // Get user counts based on role
    @GetMapping("/admin/user-counts")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getUserCounts() {
        Map<String, Integer> userCounts = new HashMap<>();

        // Get the count of users by role
        userCounts.put("generalUsers", userRepository.countByRole("user"));
        userCounts.put("volunteers", userRepository.countByRole("volunteer"));
        userCounts.put("ngos", userRepository.countByRole("ngo"));
        userCounts.put("governmentOfficials", userRepository.countByRole("government"));

        // Return the counts as a JSON response
        return ResponseEntity.ok(userCounts);
    }

    @GetMapping("/admindashboard")
    public String admindashboardPage(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");
        model.addAttribute("user", loggedInUser);

        // Fetch all registered users (Optional, if needed in the view)
        List<User> users = userRepository.findAll(); 
        model.addAttribute("users", users);

        // Fetch user counts by role
        Map<String, Integer> userCounts = new HashMap<>();
        userCounts.put("generalUsers", userRepository.countByRole("user"));
        userCounts.put("volunteers", userRepository.countByRole("volunteer"));
        userCounts.put("ngos", userRepository.countByRole("ngo"));
        userCounts.put("governmentOfficials", userRepository.countByRole("government"));

        model.addAttribute("userCounts", userCounts); // Add the counts to the model

        return "adminDashboard"; // Render the admin dashboard
    }
    @GetMapping("/vdata")
    public String vdataPage(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");
        model.addAttribute("user", loggedInUser);
        List<User> volunteers = userRepository.findByRole("volunteer"); // Fetch only volunteer users
        model.addAttribute("users", volunteers);
        return "vdata"; // Render the admin dashboard
    }
    @GetMapping("/ngodata")
    public String ngodataPage(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");
        model.addAttribute("user", loggedInUser);

        List<User> NGO = userRepository.findByRole("NGO"); // Fetch only volunteer users
        model.addAttribute("users", NGO);
        
        return "ngodata"; // Render the admin dashboard
    }
    @GetMapping("/userdata")
    public String userdataPage(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");
        model.addAttribute("user", loggedInUser);
        List<User> guser = userRepository.findByRole("user"); // Fetch only volunteer users
        List<String> roles = Arrays.asList("user", "volunteer", "government", "ngo", "admin");
        model.addAttribute("users", guser);
        model.addAttribute("roles", roles);
        return "userdata"; // Render the admin dashboard
    }
    @GetMapping("/volunteerdata")
    public String volunteerdataPage(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");
        model.addAttribute("user", loggedInUser);
        List<User> voldata = userRepository.findByRole("volunteer"); // Fetch only volunteer users
        List<String> roles = Arrays.asList("user", "volunteer", "government", "ngo", "admin");
        model.addAttribute("users", voldata);
        model.addAttribute("roles", roles);
        return "volunteerdata"; // Render the admin dashboard
    }
    @GetMapping("/NGO")
    public String NGOPage(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");
        model.addAttribute("user", loggedInUser);
        List<User> ngo = userRepository.findByRole("NGO"); // Fetch only volunteer users
        List<String> roles = Arrays.asList("user", "volunteer", "government", "ngo", "admin");
        model.addAttribute("users", ngo);
        model.addAttribute("roles", roles);
        return "NGO"; // Render the admin dashboard
    }
    @GetMapping("/govdata")
    public String govdataPage(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");
        model.addAttribute("user", loggedInUser);
        List<User> gov = userRepository.findByRole("government"); // Fetch only volunteer users
        List<String> roles = Arrays.asList("user", "volunteer", "government", "ngo", "admin");
        model.addAttribute("users", gov);
        model.addAttribute("roles", roles);
        return "govdata"; // Render the admin dashboard
    }
     // Update user role
     @PostMapping("/users/updateRole")
     public String updateUserRole(@RequestParam Long userId, @RequestParam String role) {
         User user = userRepository.findById(userId)
                 .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
         user.setRole(role); // Update the role
         userRepository.save(user); // Save the updated user
         return "redirect:/admindashboard";
     }
 
     // Delete user
     @PostMapping("/users/delete")
     public String deleteUser(@RequestParam Long userId) {
         userRepository.deleteById(userId); // Delete the user
         return "redirect:/admindashboard";
     }

    @GetMapping("/ngodashboard")
    public String ngodashboardPage(HttpSession session,Model model){
        String loggedInUser = (String) session.getAttribute("username");

        model.addAttribute("user", loggedInUser);

        return "ngoDashboard";
    }

    @GetMapping("/gdashboard")
    public String gdashboard(HttpSession session, Model model){
        String loggedInUser = (String) session.getAttribute("username");

        model.addAttribute("user", loggedInUser);
        return "gDashboard";

    }

    @GetMapping("/vdashboard")
    public String vdashboard(HttpSession session, Model model){
        String loggedInUser = (String) session.getAttribute("username");

        model.addAttribute("user", loggedInUser);
        return "vDashboard";

    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String loggedInUser = (String) session.getAttribute("username");

        if (loggedInUser == null) {
            return "redirect:/userSignin"; // Redirect to signin if not logged in
        }

        model.addAttribute("user", loggedInUser);
        return "userDashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/userSignin";
    }

}
