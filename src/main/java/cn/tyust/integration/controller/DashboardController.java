package cn.tyust.integration.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.tyust.integration.service.ClockInService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.tyust.integration.dto.ReportDTO;
import cn.tyust.integration.pojo.ClockIn;
import cn.tyust.integration.pojo.User;
import cn.tyust.integration.service.UserService;
import cn.tyust.integration.utils.LoginUtils;
import cn.tyust.integration.utils.MyProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class DashboardController {

    private UserService userService;
    private ClockInService clockInService;
    private LoginUtils loginUtils;
    private MyProps myProps;
    private List<ReportDTO> report = new ArrayList<>();
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public DashboardController(UserService userService,
                               ClockInService clockInService,
                               LoginUtils loginUtils,
                               MyProps myProps,
                               StringRedisTemplate stringRedisTemplate){
        this.userService = userService;
        this.clockInService = clockInService;
        this.loginUtils = loginUtils;
        this.myProps = myProps;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @RequestMapping(value = "/dashboard")
    public String toDashBoard(Model model,
                              HttpServletRequest request) throws Exception {
        int userCount = userService.countUser();
        model.addAttribute("userCount",userCount);
        loginUtils.isLoginStatus(request);
        int id = loginUtils.getId();
        User user = userService.selectUserByUID(id);
        model.addAttribute("user",user);
        String time = DateUtil.format(DateUtil.date(), "HH");
        String hiMsg = timeHi(Integer.parseInt(time));
        model.addAttribute("hiMsg",hiMsg);
        //??????????????????
        boolean isClockIn = checkClockIn(id);
        model.addAttribute("isClockIn",isClockIn);
        //?????????????????? ?????????model
        if (report.isEmpty()){
            getReport();
        }
        ReportDTO china = report.get(0);
        ReportDTO global = report.get(1);
        model.addAttribute("china",china);
        model.addAttribute("global",global);
        return "dashboard";
    }

    @RequestMapping(value = "/dashboard/doClockIn",method = RequestMethod.POST)
    public String doClockIn(@RequestParam("temperature")float temperature,
                            @RequestParam("bmStatus") int bmStatus,
                            HttpServletRequest request){

        if (loginUtils.isLoginStatus(request)){
            if(!checkClockIn(loginUtils.getId())){
                ClockIn clockIn = new ClockIn();
                int uID = loginUtils.getId();
                clockIn.setBmStatus(bmStatus);
                clockIn.setUID(uID);
                clockIn.setTemperature(temperature);
                clockIn.setTime(new Date());
                //??????
                clockInService.addClockIn(clockIn);
                return "redirect:/dashboard";
            }else {
                return "redirect:/dashboard";
            }

        }else {
            return "redirect:/login";
        }

    }


    /**
     * ??????????????????????????????????????????????????????????????????????????????
     * @throws Exception
     */
    @Scheduled(cron = "0 0/30 * * * *")
    private void getReport() throws Exception {
        String  COVID_19_report = null;
        List<ReportDTO> list = new ArrayList<>();
        ReportDTO chinaStatistics = new ReportDTO();
        ReportDTO globalStatistics = new ReportDTO();
        try {
            COVID_19_report = HttpUtil.get(myProps.getUrl() );
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception("?????????????????????");
        }
        if (COVID_19_report != null){
            try {
                JSONObject object = JSON.parseObject(COVID_19_report);
                JSONArray array = object.getJSONArray("results");
                JSONObject results = (JSONObject) array.get(0);
                chinaStatistics.setGeneralRemark((String) results.get("generalRemark"));
                chinaStatistics.setRemark1((String) results.get("remark1"));
                chinaStatistics.setRemark2((String) results.get("remark2"));
                chinaStatistics.setRemark3((String) results.get("remark3"));
                chinaStatistics.setNote1((String) results.get("note1"));
                chinaStatistics.setNote2((String) results.get("note2"));
                chinaStatistics.setNote3((String) results.get("note3"));
                chinaStatistics.setCurrentConfirmedCount((int) results.get("currentConfirmedCount"));
                chinaStatistics.setCurrentConfirmedCountIncr((int) results.get("currentConfirmedIncr"));
                chinaStatistics.setConfirmedCount((int) results.get("confirmedCount"));
                chinaStatistics.setConfirmedCountIncr((int) results.get("confirmedIncr"));
                chinaStatistics.setSuspectedCount((int) results.get("suspectedCount"));
                chinaStatistics.setSuspectedCountIncr((int) results.get("suspectedIncr"));
                chinaStatistics.setCuredCount((int) results.get("curedCount"));
                chinaStatistics.setCuredCountIncr((int) results.get("curedIncr"));
                chinaStatistics.setDeadCount((int) results.get("deadCount"));
                chinaStatistics.setDeadCountIncr((int) results.get("deadIncr"));
                chinaStatistics.setSeriousCount((int) results.get("seriousCount"));
                chinaStatistics.setSeriousCountIncr((int) results.get("seriousIncr"));
                chinaStatistics.setUpdateTime((long) results.get("updateTime"));
                list.add(chinaStatistics);
                JSONObject globalStatisticsJson = (JSONObject) results.get("globalStatistics");
                globalStatistics.setCurrentConfirmedCount((Integer) globalStatisticsJson.get("currentConfirmedCount"));
                globalStatistics.setCurrentConfirmedCountIncr((Integer) globalStatisticsJson.get("currentConfirmedIncr"));
                globalStatistics.setCuredCount((Integer) globalStatisticsJson.get("curedCount"));
                globalStatistics.setCuredCountIncr((Integer) globalStatisticsJson.get("curedIncr"));
                globalStatistics.setConfirmedCount((Integer) globalStatisticsJson.get("confirmedCount"));
                globalStatistics.setConfirmedCountIncr((Integer) globalStatisticsJson.get("confirmedIncr"));
                globalStatistics.setDeadCount((Integer) globalStatisticsJson.get("deadCount"));
                globalStatistics.setDeadCountIncr((Integer) globalStatisticsJson.get("deadIncr"));
                list.add(globalStatistics);
                report =  list;
                System.out.println("????????????????????????");
                //?????????redis???
                stringRedisTemplate.opsForValue().set("COVID19report",COVID_19_report);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("?????????????????????????????????");
            }

        }else {
            System.out.println("?????????????????????,????????????????????????");
        }
    }

    /**
     * ???????????????????????????
     * @param now
     * @return
     */
    private String timeHi(int now){
        if (now < 4){
            return "?????????..";
        }else if (now < 7){
            return "??????..";
        }else if (now < 11){
            return "?????????!";
        }else if (now < 13){
            return "?????????!";
        }else if (now < 17){
            return "?????????!";
        }else if (now < 19){
            return "?????????!";
        }else{
            return "?????????..";
        }
    }

    /**
     * ??????????????????????????????
     * @param uID
     * @return
     */
    private boolean checkClockIn(int uID){
        //???????????????????????????
        ClockIn clockIn = clockInService.querySomeoneNewOneClockIn(uID);
        if (clockIn != null){
            return DateUtil.isSameDay(clockIn.getTime(), new Date());
        }else {
            return false;
        }
    }

}
