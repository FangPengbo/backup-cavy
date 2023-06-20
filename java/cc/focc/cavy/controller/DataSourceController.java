package cc.focc.cavy.controller;

import cc.focc.cavy.model.common.ResultResponse;
import cc.focc.cavy.model.vo.DataSourceVO;
import cc.focc.cavy.service.DataSourceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dataSource")
public class DataSourceController {


    @Autowired
    private DataSourceService dataSourceService;


    @PutMapping
    public ResponseEntity<ResultResponse> update(@RequestBody DataSourceVO dataSourceVO){
        return ResponseEntity.ok(ResultResponse.success(dataSourceService.update(dataSourceVO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponse> delete(@PathVariable Long id){
        return ResponseEntity.ok(ResultResponse.success(dataSourceService.delete(id)));
    }

    @PostMapping
    public ResponseEntity<ResultResponse> create(@RequestBody DataSourceVO dataSourceVO){
        return ResponseEntity.ok(ResultResponse.success(dataSourceService.save(dataSourceVO)));
    }

    @GetMapping
    public ResponseEntity<ResultResponse> listPage(@RequestParam Integer page, @RequestParam Integer size){
        return ResponseEntity.ok(ResultResponse.success(dataSourceService.listPage(page,size)));
    }


}
