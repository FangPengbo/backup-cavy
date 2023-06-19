package cc.focc.cavy.controller;

import cc.focc.cavy.model.common.ResultResponse;
import cc.focc.cavy.model.vo.BackUpJobVO;
import cc.focc.cavy.service.BackUpJobService;
import cc.focc.cavy.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/backUpJob")
public class BackUpJobController {


    @Autowired
    private BackUpJobService backUpJobService;

    @PostMapping
    public ResponseEntity<ResultResponse> create(@RequestBody BackUpJobVO backUpJobVO){
        return ResponseEntity.ok(ResultResponse.success(backUpJobService.save(backUpJobVO)));
    }


}
