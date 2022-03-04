package cn.tyust.integration.service;

import cn.tyust.integration.bean.DataBean;
import cn.tyust.integration.mapper.DataMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class DataServiceImpl extends ServiceImpl<DataMapper, DataBean>
        implements DataService {


}
