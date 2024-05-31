package homemaking.controller;

import com.github.pagehelper.PageInfo;
import homemaking.model.Community;
import homemaking.service.CommunityService;
import homemaking.utils.Result;
import homemaking.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/community")
public class CommunityController {
    @Autowired
    CommunityService communityService;


    @PostMapping(value = "/add")
    public Result<Community> addCommunity(@RequestBody Community community) {
        if (communityService.addCommunity(community) != null) {
            return Result.success(community);
        } else {
            return Result.failure(ResultCodeEnum.USER_IS_EXITES);
        }
    }


    @GetMapping("/get")
    public Result getCommunity(@RequestParam(name = "topicid") Integer topicid,
                              @RequestParam(name = "pageNum") Integer pageNum,
                              @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Community> pageInfo = communityService.getCommunity(topicid,pageNum,pageSize);
        return Result.success(pageInfo);
    }

    @DeleteMapping("/deleteone/{cid}")
    public Result deleteOneCommunity(@PathVariable Integer cid)
    {
        int res = communityService.deleteOneCommunity(cid);
        if(res >= 1)
        {
            return Result.success();
        }
        else
        {
            return Result.failure(ResultCodeEnum.FAIL,"删除失败！");
        }
    }

    @PutMapping("/update")
    public Result updateCommunity(@RequestBody Community community) {
        boolean isSuccess = communityService.updateCommunity(community);
        if (isSuccess) {
            return Result.success();
        } else {
            return Result.failure(ResultCodeEnum.FAIL, "更新失败！");
        }
    }

    @GetMapping("/all")
    public Result getAllCommunity(@RequestParam(name = "pageNum") Integer pageNum,
                              @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Community> pageInfo = communityService.getAllCommunity(pageNum,pageSize);
        return Result.success(pageInfo);
    }
}
