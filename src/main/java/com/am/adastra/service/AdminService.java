package com.am.adastra.service;

import com.am.adastra.entity.Admin;
import com.am.adastra.entity.UserDBO;
import com.am.adastra.entity.dto.AdminDTO;
import com.am.adastra.entity.vo.AdminVO;
import com.am.adastra.entity.vo.AdviseVO;
import com.am.adastra.entity.vo.UserVO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Service
public interface AdminService {

    AdminVO login(Admin admin);

    Admin isLogin(HttpSession session);

    List<UserVO> selectUser(int cur, int pageSize, String username);

    int updateUser(UserDBO userDBO);

    Integer selectTotal();

    void changeState(Long uid);

    Map<String, Object> members();

    Map<String, Object> videoHeat();

    Map<String, Object> videoHeatUser(Long uid);

    void updateAdmin(AdminDTO adminDTO);

    Map<String, Object> viewingPeriod();

    Map<String, Object> numberPersons();

    List<AdviseVO> getAllAdvise();
}
