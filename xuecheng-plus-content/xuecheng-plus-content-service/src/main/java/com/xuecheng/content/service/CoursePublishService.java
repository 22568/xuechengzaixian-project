package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;

import java.io.File;

public interface CoursePublishService {
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);
    public void commitAudit(Long companyId,Long courseId);
    public void publish(Long companyId,Long courseId);
    public File generateCourseHtml(Long courseId);
    public void  uploadCourseHtml(Long courseId,File file);

    CoursePublish getCoursePublish(Long courseId);
    public CoursePublish getCoursePublishCache(Long courseId);
}
