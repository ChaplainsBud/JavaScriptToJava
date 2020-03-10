package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String homePage(Model model){
        model.addAttribute("messages", messageRepository.findAll());
        model.addAttribute("images", imageRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String messageForm(Model model){
        model.addAttribute("message", new Message());
        model.addAttribute("image", new Image());
        return "messageform";
    }

    @PostMapping("/add")
    public String processImage(@ModelAttribute Image image,
                               @RequestParam("file")MultipartFile file){
        if(file.isEmpty()){
            return "redirect:/add";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                   ObjectUtils.asMap("resourcetype", "auto"));
            image.setHeadshot(uploadResult.get("url").toString());
            imageRepository.save(image);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }
     return "redirect:/" ;}

    @PostMapping("/process")
    public String processForm(@Valid Message message,
                              BindingResult result){
        if (result.hasErrors()){
            return "messageform";
        }
        messageRepository.save(message);
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id,
                                Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }

    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/";
    }
}
