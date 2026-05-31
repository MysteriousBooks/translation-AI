package com.translation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.translation.dto.admin.AnnouncementDTO;
import com.translation.entity.Announcement;
import com.translation.mapper.AnnouncementMapper;
import com.translation.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public Page<Announcement> getAppNoticeList(int page, int size) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        /* 只显示已发布的 */
        wrapper.eq(Announcement::getStatus, 1);
        wrapper.orderByDesc(Announcement::getPublishTime);
        return announcementMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Page<Announcement> getAdminNoticeList(int page, int size) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Announcement::getCreateTime);
        return announcementMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void createAnnouncement(AnnouncementDTO dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setType(dto.getType() != null ? dto.getType() : 1);
        announcement.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        if (announcement.getStatus() == 1) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        announcementMapper.insert(announcement);
    }

    @Override
    public void updateAnnouncement(Long id, AnnouncementDTO dto) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            return;
        }
        if (dto.getTitle() != null) {
            announcement.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            announcement.setContent(dto.getContent());
        }
        if (dto.getType() != null) {
            announcement.setType(dto.getType());
        }
        if (dto.getStatus() != null) {
            announcement.setStatus(dto.getStatus());
            if (dto.getStatus() == 1 && announcement.getPublishTime() == null) {
                announcement.setPublishTime(LocalDateTime.now());
            }
        }
        announcementMapper.updateById(announcement);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }
}