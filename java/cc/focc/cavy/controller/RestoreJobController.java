package cc.focc.cavy.controller;

import cc.focc.cavy.model.common.ResultResponse;
import cc.focc.cavy.model.vo.RestoreJobVO;
import cc.focc.cavy.service.RestoreJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restoreJob")
public class RestoreJobController {

    @Autowired
    RestoreJobService restoreJobService;

    @GetMapping
    public ResponseEntity<ResultResponse> list(@RequestParam Integer page, @RequestParam Integer size, RestoreJobVO restoreJobVO){
        return ResponseEntity.ok(ResultResponse.success(restoreJobService.list(page,size,restoreJobVO)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> recordList(@PathVariable Long id){
        return ResponseEntity.ok(ResultResponse.success(restoreJobService.recordList(id)));
    }


}
