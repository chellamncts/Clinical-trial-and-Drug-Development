package org.example.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.example.model.SampleLog;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

//
//@RestController
//@RequestMapping("/users")
//public class UserController {
//
//    @Autowired
//    private UserService service;
//
//    @PostMapping("/added")
//    public User addUser(@RequestBody User user) {
//
//        return service.saveUser(user);
//    }
//
//    @GetMapping
//    public List<User> getUsers() {
//        return service.getAllUsers();
//
//    }
//}
@Controller
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/UserForm")
    public String showForm(Model model) {
        model.addAttribute("user", new SampleLog());
        return "UserForm";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute SampleLog user, Model model) {
        System.out.println("HI");
        service.saveSampleLog(user);
        int id = user.getSampleId();
        model.addAttribute("subId", id);
        return "result";
    }
}
