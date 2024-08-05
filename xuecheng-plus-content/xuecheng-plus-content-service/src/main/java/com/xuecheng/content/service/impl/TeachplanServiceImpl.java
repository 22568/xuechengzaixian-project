package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
@Slf4j
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }
    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        Long Id = teachplanDto.getId();
        if (Id != null) {
            Teachplan teachplan = teachplanMapper.selectById(Id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }else {
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            BeanUtils.copyProperties(teachplanDto,teachplanNew);
            //设置排序号
            teachplanNew.setOrderby(count+1);
            teachplanMapper.insert(teachplanNew);
        }
    }
    @Transactional
    @Override
    public void deleteTeachplan(long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            throw new XueChengPlusException("课程计划不存在");
        }
        if(teachplan.getParentid()==0&&!getSon(teachplan.getCourseId(),teachplanId).isEmpty()){
            throw new XueChengPlusException("课程计划信息还有子级信息，无法操作");
        }
        List<TeachplanMedia> teachplanMedia = teachplanMediaMapper.selectList(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId));
        if(teachplanMedia!=null&& !teachplanMedia.isEmpty()){
            for (TeachplanMedia media : teachplanMedia) {
                teachplanMediaMapper.deleteById(media.getId());
            }
        }
        teachplanMapper.deleteById(teachplanId);
    }
    @Transactional
    @Override
    public void moveUpTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);

        Integer orderby = teachplan.getOrderby();
        if(orderby >1){
            Teachplan oldteachplan = getOrder( teachplan.getCourseId(),teachplan.getParentid(), orderby - 1);
            teachplan.setOrderby(orderby -1);
            teachplanMapper.updateById(teachplan);
            oldteachplan.setOrderby(orderby);
            teachplanMapper.updateById(oldteachplan);
        }else {
            throw new XueChengPlusException("该位置无法上移");
        }

    }

    @Transactional
    @Override
    public void moveDownTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer orderby = teachplan.getOrderby();
        if(orderby >0&&orderby<getSon(teachplan.getCourseId(),teachplan.getParentid()).size()){
            Teachplan oldteachplan = getOrder(teachplan.getCourseId(),teachplan.getParentid(), orderby + 1);
            teachplan.setOrderby(orderby + 1);
            teachplanMapper.updateById(teachplan);
            oldteachplan.setOrderby(orderby);
            teachplanMapper.updateById(oldteachplan);
        }else {
            throw new XueChengPlusException("该位置无法下移");
        }
    }
    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        if(teachplan.getGrade()!=2){
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
        Long courseId = teachplan.getCourseId();
        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));
        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }
    @Transactional
    @Override
    public void deleteMedia(Long teachPlanId, Long mediaId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        if(teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        if(teachplan.getGrade()!=2){
            XueChengPlusException.cast("只允许第二级教学计划删除媒资文件");
        }
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId,teachPlanId);
        queryWrapper.eq(TeachplanMedia::getMediaId,mediaId);
        teachplanMediaMapper.delete(queryWrapper);
    }

    private Teachplan getOrder(Long courseId, Long parentId,int orderby){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        queryWrapper.eq(Teachplan::getOrderby,orderby);
        return teachplanMapper.selectOne(queryWrapper);
    }
    private List<Teachplan> getSon(Long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        return teachplanMapper.selectList(queryWrapper);
    }

    private int getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        return teachplanMapper.selectCount(queryWrapper);
    }
}
