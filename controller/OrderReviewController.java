package homemaking.controller;

import com.github.pagehelper.PageInfo;
import homemaking.model.OrderReview;
import homemaking.service.OrderReviewService;
import homemaking.utils.Result;
import homemaking.utils.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "5-订单审批接口")
@RestController
@RequestMapping("/orderreview")
public class OrderReviewController {
    @Autowired
    OrderReviewService orderReviewService;
    @ApiOperation("分页获取订单审批列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum",value = "当前页码",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize",value = "当前页数量",dataTypeClass = Integer.class,required = true,defaultValue = "4"),
    })
    @GetMapping("/all")
    public Result getAllOrderReviews(@RequestParam(name = "pageNum") Integer pageNum,
                                     @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<OrderReview> pageInfo = orderReviewService.getAllOrderReviews(pageNum,pageSize);
        return Result.success(pageInfo);
    }

    @ApiOperation("根据名称模糊查找商品")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "goodsname",value = "商品名称",dataTypeClass = String.class,required = true,defaultValue = "水果"),

            @ApiImplicitParam(name = "pageNum",value = "当前页码",dataTypeClass = Integer.class,required = true,defaultValue = "1"),

            @ApiImplicitParam(name = "pageSize",value = "当前页数量",dataTypeClass = Integer.class,required = true,defaultValue = "4"),
    })
    @GetMapping("/search")
    public Result search( @RequestParam(name = "goodsname") String goodsname,
                          @RequestParam(name = "pageNum") Integer pageNum,
                          @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<OrderReview> pageInfo = orderReviewService.searchGoodsByName(goodsname,pageNum,pageSize);
        return Result.success(pageInfo);
    }

    @ApiOperation("根据商品id获取商品详情")
    @ApiImplicitParam(name = "gid",value = "商品id",dataTypeClass = String.class,required = true,defaultValue = "1")
    @GetMapping("/detail/{oid}")
    public Result detail( @PathVariable(name = "oid")Integer oid)
    {
        OrderReview goods = orderReviewService.getOrderReviewById(oid);
        return Result.success(goods);
    }

    @ApiOperation("发布订单")
    @PostMapping(value = "/release")
    public Result<OrderReview> release(@RequestBody OrderReview orderReview) {
        if (orderReviewService.insertOrderReview(orderReview) != null) {
            return Result.success(orderReview);
        } else {
            return Result.failure(ResultCodeEnum.USER_IS_EXITES);
        }
    }

    @ApiOperation("根据状态列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "state",value = "订单状态",dataTypeClass = String.class,required = true,defaultValue = "通过"),

            @ApiImplicitParam(name = "pageNum",value = "当前页码",dataTypeClass = Integer.class,required = true,defaultValue = "1"),

            @ApiImplicitParam(name = "pageSize",value = "当前页数量",dataTypeClass = Integer.class,required = true,defaultValue = "4"),
    })
    @GetMapping("/state")
    public Result state( @RequestParam(name = "state") String state,
                          @RequestParam(name = "pageNum") Integer pageNum,
                          @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<OrderReview> pageInfo = orderReviewService.getOrderReviewsByState(state,pageNum,pageSize);
        return Result.success(pageInfo);
    }


    @ApiOperation("生成订单")
    @PostMapping("/update")
    public Result<OrderReview> update(@RequestBody OrderReview orderReview) {
            try {
                boolean updateResult = orderReviewService.updateOrderReview(orderReview);

                if (updateResult) {
                    return Result.success(orderReview);
                } else {
                    return Result.failure(ResultCodeEnum.USER_IS_EXITES);
                }
            } catch (Exception e) {
                // 处理异常，可以记录日志等
                return Result.failure(ResultCodeEnum. INTERNAL_SERVER_ERROR,"更新商品信息时发生错误：");
            }
        }

    @GetMapping("/UserReviews")
    public Result getAllUserReviews(@RequestParam(name = "userid") Integer userid,
                              @RequestParam(name = "pageNum") Integer pageNum,
                              @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<OrderReview> pageInfo = orderReviewService.getAllUserReviews(userid,pageNum,pageSize);
        return Result.success(pageInfo);
    }


    @DeleteMapping("/deleteone/{oid}")
    @ApiImplicitParam(name = "oid",value = "订单id",dataTypeClass = Integer.class,required = true,defaultValue = "1")
    public Result deldeteOne(@PathVariable Integer oid)
    {
        int res = orderReviewService.deleteOneOrderReviews(oid);
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
        int res = orderReviewService.deleteAllOrderReviews(userid);
        if(res >= 1)
        {
            return Result.success();
        }
        else
        {
            return Result.failure(ResultCodeEnum.FAIL,"清空失败！");
        }
    }
}


