package homemaking.controller;// 省略导入语句

import com.github.pagehelper.PageInfo;
import homemaking.model.User;
import homemaking.service.UserService;
import homemaking.utils.JWTUtils;
import homemaking.utils.Result;
import homemaking.utils.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import static homemaking.utils.Result.success;

@Api(tags = "1-用户接口")
@RestController
@RequestMapping(value = "/user", produces = "application/json; charset=utf-8")
// 127.0.0.1:8080/user/
public class UserController {

    @Autowired
    UserService userService;

    // 注册接口
    @ApiOperation("用户注册")
    @PostMapping(value = "/register")
    public Result<User> register(@RequestBody User user) {
        if (userService.registerService(user) != null) {
            return success(user);
        } else {
            return Result.failure(ResultCodeEnum.USER_IS_EXITES);
        }
    }

    // 用户登录接口
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ResponseEntity<Result> login(@RequestBody User user) {
        User userFromJdbc = userService.loginService(user.getUsername(), user.getPassword(), user.getUserType());
        if (userFromJdbc == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                    .header("X-Content-Type-Options", "nosniff")
                    .body(Result.failure(ResultCodeEnum.UNAUTHORIZED, "用户名或密码错误！"));
        } else {
            String token = JWTUtils.getToken(String.valueOf(userFromJdbc.getUserid()), userFromJdbc.getUsername());
            Map<String, String> userMap = new HashMap<>();
            userMap.put("userId", String.valueOf(userFromJdbc.getUserid()));
            userMap.put("userName", userFromJdbc.getUsername());
            userMap.put("userType", userFromJdbc.getUserType().toString());
            userMap.put("token", token);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                    .header("X-Content-Type-Options", "nosniff")
                    .body(success(userMap));
        }
    }

    @DeleteMapping("/deleteOneuser/{userid}")
    public Result deleteOneuser(@PathVariable Integer userid)
    {
        int res = userService.deleteOneuser(userid);
        if(res >= 1)
        {
            return success();
        }
        else
        {
            return Result.failure(ResultCodeEnum.FAIL,"删除失败！");
        }
    }

    @GetMapping("/detail/{userid}")
    public Result getUserById( @PathVariable(name = "userid")Integer userid)
    {
        User user = userService.getUserById(userid);
        return success(user);
    }


    @GetMapping("/all")
    public Result getAlluser(@RequestParam(name = "pageNum") Integer pageNum,
                              @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<User> pageInfo = userService.getAlluser(pageNum,pageSize);
        return success(pageInfo);
    }


    @PutMapping("/edit/{userid}")
    public Result updateUser(@PathVariable(name = "userid") Integer userid, @RequestBody User updateUser) {
        if (userid == null || updateUser == null) {
            return Result.failure(ResultCodeEnum.UNAUTHORIZED, "用户不存在或更新信息不能为空");
        }
        User originalUser = userService.getUserById(userid);
        if (originalUser == null) {
            return Result.failure(ResultCodeEnum.NOT_FOUND, "未找到对应的商品信息");
        }
        try {
            originalUser.setPassword(updateUser.getPassword());
            originalUser.setPhonenumber(updateUser.getPhonenumber());
            originalUser.setEmail(updateUser.getEmail());
            originalUser.setAddress(updateUser.getAddress());
            // 调用服务层进行更新
            boolean isSuccess = userService.updateUser(originalUser);
            if (isSuccess) {
                return success("用户信息更新成功");
            } else {
                return Result.failure(ResultCodeEnum.NOT_FOUND, "用户信息更新失败");
            }
        } catch (DataAccessException e) {
            // 捕获与数据库交互相关的异常（DataAccessException是Spring的通用数据库异常）
            return handleDatabaseException(e);
        } catch (Exception e) {
            // 处理其他可能的异常
            return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR, "更新用户信息时发生错误：" + e.getMessage());
        }
    }

    private Result handleDatabaseException(DataAccessException e) {
        // 具体处理数据库交互异常的逻辑

        if (e.getCause() instanceof ConstraintViolationException) {
            // 处理违反数据库约束的异常
            return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR, "数据库约束违反");
        } else if (e.getCause() instanceof DataIntegrityViolationException) {
            // 处理数据完整性违反的异常
            return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR, "数据完整性违反");
        } else {
            // 其他数据库相关异常的通用处理
            return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR, "数据库交互异常：" + e.getMessage());
        }
    }

    @PostMapping("/updateBalance")
    public Result updateBalance(@RequestBody User.BalanceUpdateRequest request) {
        boolean result = userService.updateBalance(request.getUserid(), request.getBalance());
        // 可以根据实际情况处理更新结果，例如记录日志或返回响应给客户端
        if (result) {
            // 更新成功的逻辑
            System.out.println("Balance update successful.");
            return Result.success();
        } else {
            // 更新失败的逻辑，因为找不到用户
            System.out.println("Balance update failed. User not found.");
            return Result.failure(ResultCodeEnum.FAIL,"更新失败！");
        }
    }



}
