package homemaking.controller;

import com.github.pagehelper.PageInfo;
import homemaking.model.Skill;
import homemaking.service.SkillService;
import homemaking.utils.Result;
import homemaking.utils.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "技能接口")
@RestController
@RequestMapping("/skills")
public class SkillController {
    @Autowired
    SkillService skillService;

    @ApiOperation("根据技能ID获取技能详情")
    @ApiImplicitParam(name = "sid", value = "技能ID", dataTypeClass = Integer.class, required = true)
    @GetMapping("/{sid}")
    public Result getSkillById(@PathVariable Integer sid) {
        Skill skill = skillService.getSkillById(sid);
        if (skill != null) {
            return Result.success(skill);
        } else {
            return Result.failure(ResultCodeEnum.PARAMS_IS_BLANK);
        }
    }

    @ApiOperation("分页获取所有技能列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataTypeClass = Integer.class, required = true, defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "当前页数量", dataTypeClass = Integer.class, required = true, defaultValue = "4")
    })
    @GetMapping("/all")
    public Result getAllSkills(@RequestParam(name = "pageNum") Integer pageNum,
                               @RequestParam(name = "pageSize") Integer pageSize) {
        PageInfo<Skill> pageInfo = skillService.getAllSkills(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @ApiOperation("根据技能名称模糊搜索技能")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "sname", value = "技能名称", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "pageNum", value = "当前页码", dataTypeClass = Integer.class, required = true, defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "当前页数量", dataTypeClass = Integer.class, required = true, defaultValue = "4")
    })
    @GetMapping("/search")
    public Result searchSkillsByName(@RequestParam(name = "sname") String sname,
                                     @RequestParam(name = "pageNum") Integer pageNum,
                                     @RequestParam(name = "pageSize") Integer pageSize) {
        PageInfo<Skill> pageInfo = skillService.searchSkillsByName(sname, pageNum, pageSize);
        return Result.success(pageInfo);
    }
}

