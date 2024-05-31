package homemaking.controller;

import com.github.pagehelper.PageInfo;
import homemaking.model.Goods;
import homemaking.service.GoodsService;
import homemaking.utils.Result;
import homemaking.utils.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Api(tags = "2-商品接口")
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    GoodsService goodsService;

    @Value("${file.save-path}")
    String SavePath;


    @ApiOperation("分页获取商品首页列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum",value = "当前页码",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize",value = "当前页数量",dataTypeClass = Integer.class,required = true,defaultValue = "4"),
    })
    @GetMapping("/all")
    public Result getAllgoods(@RequestParam(name = "pageNum") Integer pageNum,
                              @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Goods> pageInfo = goodsService.getAllgoods(pageNum,pageSize);
        return Result.success(pageInfo);
    }

    @ApiOperation("根据商品id获取商品详情")
    @ApiImplicitParam(name = "gid",value = "商品id",dataTypeClass = String.class,required = true,defaultValue = "1")
    @GetMapping("/detail/{gid}")
    public Result detail( @PathVariable(name = "gid")Integer gid)
    {
        Goods goods = goodsService.getGoodById(gid);
        return Result.success(goods);
    }

    @ApiOperation("商品首页搜索-根据名称模糊查找商品")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "gname",value = "商品名称",dataTypeClass = String.class,required = true,defaultValue = "水果"),

            @ApiImplicitParam(name = "pageNum",value = "当前页码",dataTypeClass = Integer.class,required = true,defaultValue = "1"),

            @ApiImplicitParam(name = "pageSize",value = "当前页数量",dataTypeClass = Integer.class,required = true,defaultValue = "4"),
    })
    @GetMapping("/search")
    public Result search( @RequestParam(name = "gname") String gname,
                          @RequestParam(name = "pageNum") Integer pageNum,
                          @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Goods> pageInfo = goodsService.searchGoodsByName(gname,pageNum,pageSize);
        return Result.success(pageInfo);
    }

    @ApiOperation("商品首页搜索-根据商品类型模糊查找商品")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "servicetype",value = "商品类型",dataTypeClass = String.class,required = true,defaultValue = "水果"),

            @ApiImplicitParam(name = "pageNum",value = "当前页码",dataTypeClass = Integer.class,required = true,defaultValue = "1"),

            @ApiImplicitParam(name = "pageSize",value = "当前页数量",dataTypeClass = Integer.class,required = true,defaultValue = "4"),
    })
    @GetMapping("/searchtype")
    public Result searchtype( @RequestParam(name = "servicetype") String servicetype,
                          @RequestParam(name = "pageNum") Integer pageNum,
                          @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Goods> pageInfo = goodsService.searchServiceType(servicetype,pageNum,pageSize);
        return Result.success(pageInfo);
    }

    @ApiOperation("编辑商品信息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "gid", value = "商品id", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(name = "goods", value = "商品信息", dataTypeClass = Goods.class, required = true)
    })
    @PutMapping("/edit/{gid}")
    public Result editGoods(@PathVariable(name = "gid") Integer gid, @RequestBody Goods updatedGoods) {
        // 首先，验证更新的商品信息是否合法
        if (gid == null || updatedGoods == null) {
            return Result.failure(ResultCodeEnum.UNAUTHORIZED, "商品不存在或更新信息不能为空");
        }

        // 根据商品ID从数据库中获取原始商品信息
        Goods originalGoods = goodsService.getGoodById(gid);
        if (originalGoods == null) {
            return Result.failure(ResultCodeEnum.NOT_FOUND,"未找到对应的商品信息");
        }

        try {
            // 更新原始商品信息
            originalGoods.setGprice(updatedGoods.getGprice());
            originalGoods.setGname(updatedGoods.getGname());
            originalGoods.setGdetails(updatedGoods.getGdetails());
            originalGoods.setServicetype(updatedGoods.getServicetype());


            // 调用服务层进行更新
            boolean isSuccess = goodsService.updateGoods(originalGoods);
            if (isSuccess) {
                return Result.success("商品信息更新成功");
            } else {
                return Result.failure(ResultCodeEnum.NOT_FOUND,"商品信息更新失败");
            }
        } catch (Exception e) {
            // 处理任何可能的异常
            return Result.failure(ResultCodeEnum. INTERNAL_SERVER_ERROR,"更新商品信息时发生错误：");
        }
    }

    @ApiOperation("添加商品")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "goods", value = "商品信息", dataTypeClass = Goods.class, required = true),
    })
    @PostMapping("/add")
    public Result<Integer> addGoods(@RequestBody Goods goods) {
        if (goods == null) {
            return Result.failure(ResultCodeEnum.UNAUTHORIZED, "商品信息不能为空");
        }

        // 调用GoodsService中的方法添加商品
        boolean result = goodsService.addGoods(goods);

        if (result) {
            return Result.success(goods.getGid());

        } else {
            return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR, "商品添加失败");
        }
    }


    @PutMapping("/add/{gid}")
    public String updatePicture(@PathVariable Integer gid, @RequestParam("file") MultipartFile file) {
        // 保存上传的图片到指定路径
//        String fileName = UUID.randomUUID().toString() + ".jpg";
        String fileName = "good" + gid + ".jpg";
        String filePath = SavePath + "/goods/"+ fileName;
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload image";
        }
        Goods updatePicture = new Goods();
        updatePicture.setGid(gid);
        updatePicture.setGpicture("/img/goods/"+ fileName);
        boolean success = goodsService.updatePicture(updatePicture);
        if (success) {
            return "Goods updated successfully";
        } else {
            return "Failed to update Goods";
        }
    }

    @ApiOperation("删除商品")
    @ApiImplicitParam(name = "gid",value = "商品id",dataTypeClass = Integer.class,required = true,defaultValue = "1")
    @DeleteMapping("/deleteone/{gid}")
    public Result deleteOneGoods(@PathVariable Integer gid)
    {
        int res = goodsService.deleteOneGoods(gid);
        if(res >= 1)
        {
            return Result.success();
        }
        else
        {
            return Result.failure(ResultCodeEnum.FAIL,"删除失败！");
        }
    }
}