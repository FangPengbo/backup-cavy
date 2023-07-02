package cc.focc.cavy.controller;

import cc.focc.cavy.model.common.ResultResponse;
import cc.focc.cavy.model.vo.BackUpJobVO;
import cc.focc.cavy.service.BackUpJobService;
import cc.focc.cavy.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/common")
public class CommonController {


    @Autowired
    private CommonService commonService;

    @GetMapping("/getAllDataBaseByDataSource/{dataSourceId}")
    public ResponseEntity<ResultResponse> getAllDataBaseByDataSource(@PathVariable Long dataSourceId){
        return ResponseEntity.ok(ResultResponse.success(commonService.getAllDataBaseByDataSource(dataSourceId)));
    }

    @GetMapping("/lastRunTime")
    public ResponseEntity<ResultResponse> lastRunTime(@RequestParam String cron){
        return ResponseEntity.ok(ResultResponse.success(commonService.lastRunTime(cron)));
    }


}
