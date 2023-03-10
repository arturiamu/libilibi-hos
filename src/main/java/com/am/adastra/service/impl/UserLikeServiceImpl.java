package com.am.adastra.service.impl;

import com.am.adastra.app.VideoPool;
import com.am.adastra.entity.Video;
import com.am.adastra.entity.dto.VideoOperateDTO;
import com.am.adastra.ex.RepeatException;
import com.am.adastra.mapper.UserLikeMapper;
import com.am.adastra.service.UserLikeService;
import com.am.adastra.service.VideoService;
import com.am.adastra.util.BinarySearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author : ArturiaMu KMUST-Stu
 * @Date : 2022/6/9 18:02
 * @Params : @param null
 * @Exception :
 * @Return :
 * @Description ：
 */
@Slf4j
@Component
public class UserLikeServiceImpl implements UserLikeService {
    @Resource
    UserLikeMapper userLikeMapper;

    @Resource
    VideoService videoService;

    @Override
    public void addLike(VideoOperateDTO videoOperateDTO) {
        log.info(videoOperateDTO.toString());
        VideoOperateDTO likeVideo = userLikeMapper.isLikeVideo(videoOperateDTO);
        if (likeVideo != null) {
            throw new RepeatException("重复操作");
        }
        userLikeMapper.addLike(videoOperateDTO);
        videoService.like(videoOperateDTO.getAid());
        int indexPid = VideoPool.indexPid(videoOperateDTO.getPid());
        Video video = BinarySearch.GetVideo(indexPid, Long.valueOf(videoOperateDTO.getAid()));
        video.setLike(video.getLike() + 1);
    }

    @Override
    public void cancelLike(VideoOperateDTO videoOperateDTO) {
        userLikeMapper.cancelLike(videoOperateDTO);
        videoService.unlike(videoOperateDTO.getAid());
        int indexPid = VideoPool.indexPid(videoOperateDTO.getPid());
        Video video = BinarySearch.GetVideo(indexPid, Long.valueOf(videoOperateDTO.getAid()));
        video.setLike(video.getLike() - 1);
    }

    @Override
    public boolean isLikeVideo(VideoOperateDTO videoOperateDTO) {
        VideoOperateDTO likeVideo = userLikeMapper.isLikeVideo(videoOperateDTO);
        if (likeVideo != null) {
            log.info(likeVideo.toString());
            return true;
        }
        return false;
    }
}
