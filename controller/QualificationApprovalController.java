package homemaking.controller;

import com.github.pagehelper.PageInfo;
import homemaking.model.QualificationApproval;
import homemaking.service.QualificationApprovalService;
import homemaking.utils.Result;
import homemaking.utils.ResultCodeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/qualificationApproval")

public class QualificationApprovalController {
    @Autowired
    QualificationApprovalService qualificationApprovalService;

    @Value("${file.save-path}")
    String SavePath;
    @PostMapping("/insert")
    public Result<Integer> insertQualificationApproval(@RequestBody QualificationApproval qualificationApproval) {
        if (qualificationApproval == null) {
            return Result.failure(ResultCodeEnum.UNAUTHORIZED, "信息不能为空");
        }


        boolean result = qualificationApprovalService.insertQualificationApproval(qualificationApproval);

        if (result) {
            return Result.success(qualificationApproval.getQid());

        } else {
            return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR, "上传失败");
        }
    }

    @PutMapping("/add/{qid}")
    public String updatesPicture(@PathVariable Integer qid, @RequestParam("file") MultipartFile file) {
        // 保存上传的图片到指定路径
        String fileName = "identification" + qid + ".jpg";
//        String filePath = env.getProperty("file.upload-path") + fileName;
        String filePath = SavePath + "/goods/"+ fileName;
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload image";
        }
        QualificationApproval updatesPicture = new QualificationApproval();
        updatesPicture.setQid(qid);
//        updatesPicture.setSpicture(env.getProperty("image.prefix-url") + fileName);
        updatesPicture.setSpicture("/img/goods/"+ fileName);
        boolean success = qualificationApprovalService.updatesPicture(updatesPicture);
        if (success) {
            return "Goods updated successfully";
        } else {
            return "Failed to update Goods";
        }
    }

    @GetMapping("/all")
    public Result getAllQualificationApprovals(@RequestParam(name = "pageNum") Integer pageNum,
                                               @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<QualificationApproval> pageInfo = qualificationApprovalService.getAllQualificationApprovals(pageNum, pageSize);
        PageInfo<QualificationApproval> resultPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, resultPageInfo);

        return Result.success(resultPageInfo);
    }
    @GetMapping("/get/{serviceid}")
    public Result detail( @PathVariable(name = "serviceid")Integer serviceid)
    {
        QualificationApproval qualificationApproval = qualificationApprovalService.getQualificationApprovalById(serviceid);
        return Result.success(qualificationApproval);
    }

    @DeleteMapping("/deleteone/{serviceid}")
    public Result deleteQualificationApproval(@PathVariable Integer serviceid)
    {
        int res = qualificationApprovalService.deleteQualificationApproval(serviceid);
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
    public Result updateQualificationApproval(@RequestBody QualificationApproval qualificationApproval) {
        boolean isSuccess = qualificationApprovalService.updateQualificationApproval(qualificationApproval);
        if (isSuccess) {
            return Result.success();
        } else {
            return Result.failure(ResultCodeEnum.FAIL, "更新失败！");
        }
    }

}


