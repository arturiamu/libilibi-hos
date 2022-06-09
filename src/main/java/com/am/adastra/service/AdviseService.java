package com.am.adastra.service;

import com.am.adastra.entity.Advise;
import com.am.adastra.pojo.DTO.AdviseAddDTO;
import com.am.adastra.util.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: lyy
 * @Version: 1.0
 * @JDK-version: 1.8
 * @Date: 2022/6/9 10:25
 * @Description:
 */
@Service
public interface AdviseService {
    //增加建议
    Result<Advise> add(AdviseAddDTO advise);

    //返回所有建议信息
    Result<List<Advise>> selectById(Integer userId);
}
