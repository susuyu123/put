package homemaking.controller;

import com.github.pagehelper.PageInfo;
import homemaking.model.Log;
import homemaking.service.LogService;
import homemaking.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {
    @Autowired
    LogService logService;

    @PostMapping("/add")
    public boolean addLog(@RequestBody Log log) {
        return logService.addLog(log);

    }

    @DeleteMapping("/{logid}")
    public void deleteLog(@PathVariable int logid) {
        logService.deleteLog(logid);
    }

    @GetMapping("/user/{userid}")
    public Log getLogsByUserId(@PathVariable int userid) {
        return logService.getLogsByUserId(userid);
    }

    @GetMapping("/all")
    public Result getAllLogs(@RequestParam(name = "pageNum") Integer pageNum,
                              @RequestParam(name = "pageSize") Integer pageSize)
    {
        PageInfo<Log> pageInfo = logService.getAllLogs(pageNum,pageSize);
        return Result.success(pageInfo);
    }
}
