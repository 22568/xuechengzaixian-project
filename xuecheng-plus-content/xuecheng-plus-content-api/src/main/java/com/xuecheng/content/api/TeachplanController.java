package com.xuecheng.content.api;


import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
@RestController
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;
    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
        List<TeachplanDto> teachplanDto =teachplanService.findTeachplanTree(courseId);
        return teachplanDto;
    }
    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }
    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{Id}")
    public void saveTeachplan(@PathVariable Long Id){
     teachplanService.deleteTeachplan(Id);
    }
    @ApiOperation("课程计划上移")
    @PostMapping("/teachplan/moveup/{Id}")
    public void moveUpTeachplan(@PathVariable Long Id){
        teachplanService.moveUpTeachplan(Id);
    }
    @ApiOperation("课程计划下移")
    @PostMapping("/teachplan/movedown/{Id}")
    public void moveDownTeachplan(@PathVariable Long Id){
        teachplanService.moveDownTeachplan(Id);
    }
    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }
    @ApiOperation(value = "课程计划和媒资信息绑定")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void deleteMedia(@PathVariable Long teachPlanId,Long mediaId){
        teachplanService.deleteMedia(teachPlanId,mediaId);
    }
}

