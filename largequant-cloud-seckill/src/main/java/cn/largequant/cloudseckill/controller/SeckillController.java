package cn.largequant.cloudseckill.controller;

import cn.largequant.cloudcommon.entity.JsonResult;
import cn.largequant.cloudcommon.entity.seckill.SuccessKilled;
import cn.largequant.cloudcommon.vo.SecKillVO;
import cn.largequant.cloudcommon.dto.seckill.Exposer;
import cn.largequant.cloudcommon.entity.seckill.Seckill;
import cn.largequant.cloudseckill.service.SeckillService;
import cn.largequant.cloudseckill.websocket.TimerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private TimerService timerService;

    /**
     * 秒杀列表
     */
    @GetMapping("/list")
    public String list(Model model) {
        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("list", seckillList);
        return "list";
    }

    /**
     * 详情页
     */
    @GetMapping("/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill kill = seckillService.getSeckillById(seckillId);
        if (kill == null) {
            return "forward:/seckill/list";
        }
        //包装类
        SecKillVO secKillVO = new SecKillVO();
        BeanUtils.copyProperties(kill, secKillVO);
        secKillVO.setSeckillId(kill.getId());
        secKillVO.setStartTimestamp(kill.getStartTime().getTime());
        secKillVO.setEndTimestamp(kill.getEndTime().getTime());
        model.addAttribute("seckill", secKillVO);
        // 只要秒杀未结束，则启动定时任务
        if (secKillVO.getStartTimestamp() < secKillVO.getEndTimestamp() && System.currentTimeMillis() < secKillVO.getEndTimestamp()) {
            timerService.executeTimeCountdown(secKillVO.getStartTimestamp(), secKillVO.getEndTimestamp());
        }
        return "detail";
    }

    /**
     * 暴露秒杀接口
     * 秒杀接口只有在秒杀开启的时候才暴露，防止用户提前知道固定的秒杀地址去脚本刷单
     */
    @ResponseBody
    @GetMapping(value = "/{seckillId}/exposer", produces = {"application/json;charset=UTF-8"})
    public JsonResult exposer(@PathVariable("seckillId") Long seckillId) {
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            return JsonResult.success(exposer);
        } catch (RuntimeException e) {
            log.error("【暴露秒杀接口】失败，seckillId={}", seckillId);
            return JsonResult.error(e.getMessage());
        }
    }

    /**
     * 单体架构:程序锁
     */
    @ResponseBody
    @PostMapping(value = "/{seckillId}/{md5}/executeByAopLock", produces = {"application/json;charset=UTF-8"})
    public JsonResult startAopLock(@PathVariable("seckillId") Long seckillId,
                                   @PathVariable("md5") String md5, HttpServletRequest request) {
        if (request.getAttribute("user_id") != null) {
            try {
                long userId = (long)request.getAttribute("user_id");
                SuccessKilled execution = seckillService.executeByAopLock(seckillId, userId, md5);
                //封装商品信息，json格式返回，前端的ajax接收参数并回调支付模块
                return JsonResult.success(execution);
            } catch (Exception e) {
                log.error("【秒杀】 失败，{} ", e.getMessage());
                return JsonResult.error(e.getMessage());
            }
        } else {
            return JsonResult.error("请先登录");
        }
    }

    /**
     * 单体架构:数据库乐观锁
     */
    @ResponseBody
    @PostMapping(value = "/{seckillId}/{md5}/startOptimisticLock", produces = {"application/json;charset=UTF-8"})
    public JsonResult startOptimisticLock(@PathVariable("seckillId") Long seckillId,
                                          @PathVariable("md5") String md5, HttpServletRequest request) {
        if (request.getAttribute("user_id") != null) {
            try {
                long userId = (long)request.getAttribute("user_id");
                SuccessKilled execution = seckillService.executeByOptimisticLock(seckillId, userId, md5);
                return JsonResult.success(execution);
            } catch (Exception e) {
                log.error("【秒杀】 失败，{} ", e.getMessage());
                return JsonResult.error(e.getMessage());
            }
        } else {
            return JsonResult.error("请先登录");
        }
    }

    /**
     * 单体架构:数据库悲观锁
     */
    @ResponseBody
    @PostMapping(value = "/{seckillId}/{md5}/startPessimisticLock", produces = {"application/json;charset=UTF-8"})
    public JsonResult startPessimisticLock(@PathVariable("seckillId") Long seckillId,
                                           @PathVariable("md5") String md5, HttpServletRequest request) {
        if (request.getAttribute("user_id") != null) {
            try {
                long userId = (long)request.getAttribute("user_id");
                SuccessKilled execution = seckillService.executeByPessimisticLock(seckillId, userId, md5);
                return JsonResult.success(execution);
            } catch (Exception e) {
                log.error("【秒杀】 失败，{} ", e.getMessage());
                return JsonResult.error(e.getMessage());
            }
        } else {
            return JsonResult.error("请先登录");
        }
    }

    /**
     * 分布式架构:分布式zookeeper锁
     */
    @ResponseBody
    @PostMapping(value = "/{seckillId}/{md5}/startZkLock", produces = {"application/json;charset=UTF-8"})
    public JsonResult startZkLock(@PathVariable("seckillId") Long seckillId,
                                  @PathVariable("md5") String md5, HttpServletRequest request) {
        if (request.getAttribute("user_id") != null) {
            try {
                long userId = (long)request.getAttribute("user_id");
                SuccessKilled execution = seckillService.executeByZkLock(seckillId, userId, md5);
                return JsonResult.success(execution);
            } catch (Exception e) {
                log.error("【秒杀】 失败，{} ", e.getMessage());
                return JsonResult.error(e.getMessage());
            }
        } else {
            return JsonResult.error("请先登录");
        }
    }

    /**
     * 分布式架构:分布式redis锁
     */
    @ResponseBody
    @PostMapping(value = "/{seckillId}/{md5}/startRedlock", produces = {"application/json;charset=UTF-8"})
    public JsonResult startRedlock(@PathVariable("seckillId") Long seckillId,
                                   @PathVariable("md5") String md5, HttpServletRequest request) {
        if (request.getAttribute("user_id") != null) {
            try {
                long userId = (long)request.getAttribute("user_id");
                SuccessKilled execution = seckillService.executeByRedlock(seckillId, userId, md5);
                return JsonResult.success(execution);
            } catch (Exception e) {
                log.error("【秒杀】 失败，{} ", e.getMessage());
                return JsonResult.error(e.getMessage());
            }
        } else {
            return JsonResult.error("请先登录");
        }
    }

}


