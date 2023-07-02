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

    @PutMapping
    public ResponseEntity<ResultResponse> update(@RequestBody BackUpJobVO backUpJobVO){
        return ResponseEntity.ok(ResultResponse.success(backUpJobService.update(backUpJobVO)));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> list(@RequestParam Integer page, @RequestParam Integer size, BackUpJobVO backUpJobVO){
        return ResponseEntity.ok(ResultResponse.success(backUpJobService.list(page,size,backUpJobVO)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> recordList(@PathVariable Long id){
        return ResponseEntity.ok(ResultResponse.success(backUpJobService.recordList(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse> delete(@PathVariable Long id){
        return ResponseEntity.ok(ResultResponse.success(backUpJobService.delete(id)));
    }

    @GetMapping("/enable/{id}")
    public ResponseEntity<ResultResponse> enable(@PathVariable Long id){
        return ResponseEntity.ok(ResultResponse.success(backUpJobService.enable(id)));
    }



}
