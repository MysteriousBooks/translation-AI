package com.translation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.translation.dto.admin.AnnouncementDTO;
import com.translation.entity.Announcement;

public interface AnnouncementService extends IService<Announcement> {

    Page<Announcement> getAppNoticeList(int page, int size);

    Page<Announcement> getAdminNoticeList(int page, int size);

    void createAnnouncement(AnnouncementDTO dto);

    void updateAnnouncement(Long id, AnnouncementDTO dto);

    void deleteAnnouncement(Long id);
}