package cn.tyust.integration.service;

import cn.tyust.integration.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.tyust.integration.mapper.UserRoleMapper;
import cn.tyust.integration.pojo.User;
import cn.tyust.integration.pojo.UserRole;
import cn.tyust.integration.utils.EncoderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private UserMapper mapper;
    private UserRoleMapper userRoleMapper;
    private DataSourceTransactionManager transactionManager;
    private EncoderUtils encoderUtils;

    @Autowired
    public UserServiceImpl(DataSourceTransactionManager transactionManager,
                           UserMapper mapper,
                           UserRoleMapper userRoleMapper,
                           EncoderUtils encoderUtils){
        this.transactionManager = transactionManager;
        this.mapper = mapper;
        this.userRoleMapper = userRoleMapper;
        this.encoderUtils = encoderUtils;
    }

    @Override
    public PageInfo<User> selectUserList(int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        List<User> userList = mapper.selectUserList();
        return new PageInfo<>(userList,5);
    }

    @Override
    public PageInfo<User> selectUserListByName(String name, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        List<User> userList = mapper.selectUserListByName(name);

        return new PageInfo<>(userList,5);
    }

    @Override
    public PageInfo<User> selectUserListByStatus(int uStatus, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        List<User> userList = mapper.selectUserListByStatus(uStatus);
        return new PageInfo<>(userList,5);
    }

    @Override
    public User selectUserByUID(int uID) {
        return mapper.selectUserById(uID);
    }

    @Override
    public User selectUserByPhone(long phone) {
        return mapper.selectUserByPhone(phone);
    }

    @Override
    public User selectUserByEmail(String email) {
        return mapper.selectUserByEmail(email);
    }

    @Override
    public int addUser(User user) {
        //????????????
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("register-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        //???????????????
        TransactionStatus status = transactionManager.getTransaction(def);
        int i = 0;
        try {
            //??????????????????
            long createTime = System.currentTimeMillis();
            user.setCreateTime(createTime);
            user.setUpdateTime(createTime);
            mapper.addUser(user);
            //????????????
            User userForID = selectUserByPhone(user.getPhone());
            UserRole userRole = new UserRole();
            userRole.setUID(userForID.getUID());
            //????????????????????????
            userRole.setRID(1);
            i = userRoleMapper.addUserRole(userRole);
            //????????????
            transactionManager.commit(status);
        }catch (Exception e){
            e.printStackTrace();
            transactionManager.rollback(status);
        }
        return i;
    }

    @Override
    public int countUser() {
        return mapper.countUser();
    }

    @Override
    public int updateUserBasic(User user) {
        user.setUpdateTime(System.currentTimeMillis());
        return mapper.updateUserBasic(user);
    }

    @Override
    public int updateUserPwd(int id, String newPwd) {
        User user = new User();
        user.setUID(id);
        String encodePwd = encoderUtils.passwordEncoder().encode(newPwd);
        user.setUPassword(encodePwd);
        user.setUpdateTime(System.currentTimeMillis());
        return mapper.updateUserPwd(user);
    }


}
