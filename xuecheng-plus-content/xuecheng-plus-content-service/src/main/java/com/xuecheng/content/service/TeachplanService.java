package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TeachplanService {
    public List<TeachplanDto> findTeachplanTree(long courseId);
    public void saveTeachplan(SaveTeachplanDto teachplanDto);
    public void deleteTeachplan(long teachplanId);
    public void moveUpTeachplan(Long teachplanId);

    public void moveDownTeachplan(Long id);
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    void deleteMedia(Long teachPlanId, Long mediaId);
}
