package cn.largequant.cloudadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("${api-url}")
public class ApiController {

    @RequestMapping(value="/getPage")
    public ModelAndView getPage(ModelAndView modelAndView, String pageName){
        modelAndView.setViewName(pageName);
        return modelAndView;
    }

}
