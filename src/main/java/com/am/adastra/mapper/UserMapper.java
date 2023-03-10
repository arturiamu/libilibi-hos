package com.am.adastra.mapper;

import com.am.adastra.entity.User;
import com.am.adastra.entity.UserDBO;
import com.am.adastra.entity.vo.UserLoginLogVO;
import com.am.adastra.entity.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author : ArturiaMu KMUST-Stu
 * @Date : 2022/5/27 11:20
 * @Params : @param null
 * @Exception :
 * @Return :
 * @Description ：
 */
@Mapper
public interface UserMapper {

    int addDBO(UserDBO user);

    int isVip(Long uid);

    int updateDBO(UserDBO user);

    int updatePwd(String password,Long id);

    int passwordBack(String password,Long uid);

    UserDBO getDBOById(Long id);

    UserDBO getDBOByAccount(String account);

    UserDBO getDBOByUsername(String username);

    Long getUserDaoByID(String openid);

    /**
     * 返回所有用户信息
     * @return
     */
    List<UserVO> list();

    /**
     * 分页查询用户
     * @param cur
     * @param pageSize
     * @param username
     * @return
     */
    List<UserVO> selectPage(int cur, int pageSize, String username);

    /**
     * 修改用户状态
     * @param uid
     * @return
     */
    int changeState(Long uid);

    /**
     * 返回所有登录信息
     * @return
     */
    List<UserLoginLogVO> loginList();

    /**
     * 获取指定uid用户的登录日志
     * @param uid
     * @return
     */
    List<UserLoginLogVO> loginListByUid(Long uid);

    int addLoginLog(UserLoginLogVO userLoginLogVO);

    /**
     * 获取uid最新一次登录ip
     * @param uid
     * @return
     */
    UserLoginLogVO ipList(Long uid);
}
