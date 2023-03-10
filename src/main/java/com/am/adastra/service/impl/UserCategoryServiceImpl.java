package com.am.adastra.service.impl;

import com.am.adastra.entity.dto.UserCategoryAddDTO;
import com.am.adastra.entity.vo.UserCategorySimpleVO;
import com.am.adastra.ex.RepeatException;
import com.am.adastra.mapper.UserCategoryMapper;
import com.am.adastra.service.UserCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/*
 * 用户的收藏夹分类的业务逻辑层
 * */
@Slf4j
@Component
public class UserCategoryServiceImpl implements UserCategoryService {
    @Resource
    private UserCategoryMapper userCategoryMapper;

    /*
     * 新建一个收藏夹
     * */
    @Override
    public boolean add(UserCategoryAddDTO userCategoryAddDTO) {
        UserCategorySimpleVO userCategory = userCategoryMapper.selectByCategory(userCategoryAddDTO.getUid(), userCategoryAddDTO.getCategoryName());
        if (userCategory != null) {
            log.info("重复添加" + userCategory.getCategoryName());
            throw new RepeatException("收藏夹已经存在,重复添加");
        }
        String remarks = "";
        try {
            remarks = userCategoryAddDTO.getRemarks();
            userCategoryAddDTO.setRemarks(remarks);
        } catch (Exception e) {
            userCategoryAddDTO.setRemarks("愿你能在有限的时间中获取无限的快乐~");
            return userCategoryMapper.add(userCategoryAddDTO) == 1;
        }
        return userCategoryMapper.add(userCategoryAddDTO) == 1;
    }

    @Override
    public boolean clear(Long uid, String cName) {
        return userCategoryMapper.clear(uid, cName) >= 1;
    }

    @Override
    public boolean del(Long uid, String cName) {
        return userCategoryMapper.del(uid, cName) >= 1;
    }


    @Override
    public List<UserCategorySimpleVO> selectById(Long userId) {
        return userCategoryMapper.selectById(userId);
    }
}
