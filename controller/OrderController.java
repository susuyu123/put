package homemaking.controller;

import com.github.pagehelper.PageInfo;
import homemaking.model.Order;
import homemaking.service.OrderService;
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

@Api(tags = "4-订单接口")
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Value("${file.save-path}")
    String SavePath;

    @ApiOperation("分页获取订单列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userid",value = "用户id",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
            @ApiImplicitParam(name = "pageNum",value = "当前页码",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize",value = "当前页数量",dataTypeClass = Integer.class,required = true,defaultValue = "4"),
    })
    @GetMapping("/all")
    public Result getAllOrder(@RequestParam(name = "userid") Integer userid,
                              @RequestParam(name = "pageNum") Integer pageNum,
                              @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Order> pageInfo = orderService.getAllOrder(userid,pageNum,pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/serviceorder")
    public Result getServiceOrder(@RequestParam(name = "serviceid") Integer serviceid,
                              @RequestParam(name = "pageNum") Integer pageNum,
                              @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Order> pageInfo = orderService.getServiceOrder(serviceid,pageNum,pageSize);
        return Result.success(pageInfo);
    }

    @ApiOperation("删除单件订单")
    @DeleteMapping("/deleteone/{orderid}")
    @ApiImplicitParam(name = "orderid",value = "订单id",dataTypeClass = Integer.class,required = true,defaultValue = "1")
    public Result deldeteOne(@PathVariable Integer orderid)
    {
        int res = orderService.deleteOneOrder(orderid);
        if(res >= 1)
        {
            return Result.success();
        }
        else
        {
            return Result.failure(ResultCodeEnum.FAIL,"删除失败！");
        }
    }

    @ApiOperation("清空用户所有订单")
    @DeleteMapping("/deleteall/{userid}")
    @ApiImplicitParam(name = "usrid",value = "用户id",dataTypeClass = Integer.class,required = true,defaultValue = "1")
    public Result deldeteAll(@PathVariable Integer userid)
    {
        int res = orderService.deleteAllOrder(userid);
        if(res >= 1)
        {
            return Result.success();
        }
        else
        {
            return Result.failure(ResultCodeEnum.FAIL,"清空失败！");
        }
    }

    @ApiOperation("更新订单状态")
    @PutMapping("/update")
    public Result updateOrder(@RequestBody Order updatedOrder) {
        boolean isSuccess = orderService.updateOrder(updatedOrder);
        if (isSuccess) {
            return Result.success();
        } else {
            return Result.failure(ResultCodeEnum.FAIL, "更新失败！");
        }
    }


    @PutMapping("/add/{orderid}")
    public String updateorderPicture(@PathVariable Integer orderid, @RequestParam("file") MultipartFile file) {
        String fileName = "order" + orderid + ".jpg";
//        String filePath = "D:/电脑桌面/project/src/main/resources/static/img/goods/" + fileName;
        String filePath = SavePath + "/goods/"+ fileName;
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload image";
        }
        Order updateorderPicture = new Order();
        updateorderPicture.setOrderid(orderid);
        updateorderPicture.setPicture("/img/goods/"+ fileName);
        boolean success = orderService.updateorderPicture(updateorderPicture);
        if (success) {
            return "Goods updated successfully";
        } else {
            return "Failed to update Goods";
        }
    }

    @ApiOperation("获取所有用户订单")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum",value = "当前页码",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize",value = "当前页数量",dataTypeClass = Integer.class,required = true,defaultValue = "4"),
    })
    @GetMapping("/orderall")
    public Result getAlluserOrders(@RequestParam(name = "pageNum") Integer pageNum,
                              @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Order> pageInfo = orderService.getAlluserOrders(pageNum,pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/status")
    public Result status( @RequestParam(name = "status") String status,
                         @RequestParam(name = "pageNum") Integer pageNum,
                         @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Order> pageInfo = orderService.getOrderByState(status,pageNum,pageSize);
        return Result.success(pageInfo);
    }


        @PutMapping("/commit")
    public Result updateorderCommint(@RequestBody Order updateorderCommint) {
        boolean isSuccess = orderService.updateorderCommint(updateorderCommint);
        if (isSuccess) {
            return Result.success();
        } else {
            return Result.failure(ResultCodeEnum.FAIL, "更新失败！");
        }
    }
}
