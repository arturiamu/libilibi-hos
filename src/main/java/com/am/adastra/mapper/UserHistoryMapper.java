package com.am.adastra.mapper;

import com.am.adastra.entity.Video;
import com.am.adastra.entity.dto.VideoOperateDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @Author : ArturiaMu KMUST-Stu
 * @Date : 2022/6/10 13:44
 * @Params : @param null
 * @Exception :
 * @Return :
 * @Description ：
 */
@Mapper
public interface UserHistoryMapper {
    List<Video> getAll(Integer uid);

    List<Video> getLimitByDate(Integer uid, Date date);

    List<Video> getLimit(Integer uid, Integer ps);

    boolean add(VideoOperateDTO videoOperateDTO);
}
