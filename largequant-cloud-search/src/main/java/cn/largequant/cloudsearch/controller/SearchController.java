package cn.largequant.cloudsearch.controller;

import cn.largequant.cloudcommon.entity.Post;
import cn.largequant.cloudcommon.entity.Question;
import cn.largequant.cloudcommon.entity.ViewObject;
import cn.largequant.cloudsearch.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    SearchService searchService;

    @GetMapping(value = "/search")
    public String search(Model model,
                         @RequestParam("q") String keyword,
                         @RequestParam(value = "p", defaultValue = "1") int p) {
        try {
            int offset = (p - 1) * 10;
            List<Post> postList = searchService.testSearch(keyword, offset, 10);
            List<ViewObject> vos = new ArrayList<>();
            for (Post post : postList) {
                ViewObject vo = new ViewObject();
                vo.set("post", post);
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            logger.error("搜索失败" + e.getMessage());
        }
        return "result";
    }
}