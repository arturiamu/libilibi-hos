package com.am.adastra.service;

import com.am.adastra.entity.User;
import com.am.adastra.entity.UserDBO;
import com.am.adastra.entity.vo.UserLoginLogVO;
import com.am.adastra.entity.vo.UserVO;
import com.am.adastra.util.Result;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @Author : ArturiaMu KMUST-Stu
 * @Date : 2022/5/27 11:23
 * @Params : @param null
 * @Exception :
 * @Return :
 * @Description ：
 */
@Service
public interface UserService {
    User register(User user);

    User login(User user,String ip);

    User update(User user);

    User isLogin(HttpSession session);

    User updatePwd(String password,String account);

    User updateDBO(User old,User new_);

    UserDBO getDBOById(Long id);

    List<UserVO> list();

    List<UserLoginLogVO> loginList();

    List<UserLoginLogVO> loginListByUid(Long uid);

    int addLoginLog(UserLoginLogVO userLoginLogVO);

    List<Map<String,Object>> ipList();

    Long getUserDaoByID(String openid);
}
