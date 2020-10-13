package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.ConvertService;

@Controller
public class ConvertController {

    @Autowired
    private ConvertService convertService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @PostMapping("/convert")
    public String result(@RequestParam String usd, Model model) {

        if (usd.equals("")) {
            model.addAttribute("result", "Chưa nhập mà đòi tính cái gì !");
        } else {
            double result = convertService.convert(Double.parseDouble(usd));
            model.addAttribute("result", "Result : " + result + " VND .");
        }

        return "home";
    }
}