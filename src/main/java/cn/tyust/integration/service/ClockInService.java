package cn.tyust.integration.service;

import com.github.pagehelper.PageInfo;
import cn.tyust.integration.pojo.ClockIn;

public interface ClockInService {

    PageInfo<ClockIn> queryAllClockIn(int page,int pageSize);
    PageInfo<ClockIn> querySomeoneClockIn(int uID, int page, int pageSize);
    ClockIn querySomeoneNewOneClockIn(int uID);
    int addClockIn(ClockIn clockIn);

}
