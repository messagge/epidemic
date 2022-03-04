package cn.tyust.integration.service;

import cn.tyust.integration.pojo.UserRole;

import java.util.List;

public interface UserRoleService {

    List<Integer> selectUserRoleByUid(int uID);
    int addRole(UserRole userRole);

    int deleteRole(UserRole userRole);
}
