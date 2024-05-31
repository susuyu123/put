package homemaking.controller;

import com.github.pagehelper.PageInfo;
import homemaking.model.Slider;
import homemaking.service.SliderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/sliders")
public class SliderController {

    @Value("${file.save-path}")
    String SavePath;

    @Autowired
    SliderService sliderService;

    // 获取所有轮播图数据
    @GetMapping("/all")
    public PageInfo<Slider> getAllSliders(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize)
    {
        PageInfo<Slider> pageInfo = sliderService.getAllSliders(pageNum,pageSize);
        return pageInfo;
    }

    // 编辑轮播图
    @PutMapping("/{sid}")
    public String updateSlider(@PathVariable Integer sid, @RequestParam("file") MultipartFile file) {
        // 保存上传的图片到指定路径
//        String fileName = UUID.randomUUID().toString() + ".jpg";
        String fileName = "slider" + sid + ".jpg";
        String filePath = SavePath + "/goods/" +fileName;
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload image";
        }

        // 更新轮播图的图片路径
        Slider updatedSlider = new Slider();
        updatedSlider.setSid(sid);
        updatedSlider.setSpicture("/img/goods/"+ fileName);

        boolean success = sliderService.updateSlider(updatedSlider);
        if (success) {
            return "Slider updated successfully";
        } else {
            return "Failed to update slider";
        }
    }


}
