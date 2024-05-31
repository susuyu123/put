package homemaking.controller;

import com.github.pagehelper.PageInfo;
import homemaking.model.Cart;

import homemaking.service.CartService;
import homemaking.utils.Result;
import homemaking.utils.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "3-购物车接口")
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;



    @ApiOperation("分页获取所有购物车商品")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userid",value = "用户id",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
            @ApiImplicitParam(name = "pageNum",value = "当前页码",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize",value = "当前页数量",dataTypeClass = Integer.class,required = true,defaultValue = "4"),
    })
    @GetMapping("/all")
    public Result getAllCart(@RequestParam(name = "userid") Integer userid,
                             @RequestParam(name = "pageNum") Integer pageNum,
                             @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Cart> pageInfo = cartService.getAllCart(userid,pageNum,pageSize);
        return Result.success(pageInfo);
    }

    @ApiOperation("加入购物车")
    @PostMapping("/insert")
    public Result insertCart(@RequestBody Cart cart)
    {
        Cart cart1 = cartService.insertCart(cart);
        return Result.success(cart1);
    }


    @ApiOperation("购物车加1操作")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userid",value = "用户id",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
            @ApiImplicitParam(name = "gid",value = "商品id",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
    })
    @PutMapping("/addone")
    public Result addCart(@RequestParam(name = "userid") Integer userid,
                          @RequestParam(name = "gid") Integer gid)
    {
        Cart cart = cartService.modifyNumber(userid,gid,1);
        return Result.success(cart);
    }

    @ApiOperation("购物车减1操作")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "userid",value = "用户id",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
            @ApiImplicitParam(name = "gid",value = "商品id",dataTypeClass = Integer.class,required = true,defaultValue = "1"),
    })
    @PutMapping("/subone")
    public Result subCart(@RequestParam(name = "userid") Integer userid,
                          @RequestParam(name = "gid") Integer gid)
    {
        Cart cart = cartService.modifyNumber(userid,gid,-1);
        return Result.success(cart);
    }

    @ApiOperation("购物车删除单件商品")
    @ApiImplicitParam(name = "id",value = "购物车id",dataTypeClass = Integer.class,required = true,defaultValue = "1")
    @DeleteMapping("/deleteone/{id}")
    public Result deldeteOne(@PathVariable Integer id)
    {
        int res = cartService.deleteOne(id);
        if(res >= 1)
        {
            return Result.success();
        }
        else
        {
            return Result.failure(ResultCodeEnum.FAIL,"删除失败！");
        }
    }

    @ApiOperation("清空用户购物车")
    @DeleteMapping("/deleteall/{userid}")
    @ApiImplicitParam(name = "userid",value = "用户id",dataTypeClass = Integer.class,required = true,defaultValue = "1")
    public Result deldeteAll(@PathVariable Integer userid)
    {
        int res = cartService.deleteAll(userid);
        if(res >= 1)
        {
            return Result.success();
        }
        else
        {
            return Result.failure(ResultCodeEnum.FAIL,"删除失败！");
        }
    }
    @ApiOperation("结算用户购物车")
    @PostMapping("/pay/{userid}")
    @ApiImplicitParam(name = "userid",value = "用户id",dataTypeClass = Integer.class,required = true,defaultValue = "1")
    public Result payAll(@PathVariable Integer userid)
    {
        int res = cartService.payCart(userid);
        if(res >= 1)
        {
            return Result.success();
        }
        else
        {
            return Result.failure(ResultCodeEnum.FAIL,"结算失败！");
        }
    }

//    @PostMapping("/createOrder")
//    public String createOrder(@RequestBody String orderDetails) {
//        String form;
//        try {
//            form = alipayService.createOrder(orderDetails);
//        } catch (Exception e) {
//            e.printStackTrace();
//            form = "Error in Order Creation";
//        }
//        return form;
//    }

}
