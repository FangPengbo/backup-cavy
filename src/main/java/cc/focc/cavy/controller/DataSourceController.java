package cc.focc.cavy.controller;

import cc.focc.cavy.entity.vo.DataSourceVO;
import cc.focc.cavy.service.DataSourceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("dataSource")
public class DataSourceController {


    @Autowired
    private DataSourceService dataSourceService;

    @PostMapping
    public ResponseEntity<Boolean> create(@RequestBody DataSourceVO dataSourceVO){
        return ResponseEntity.ok(dataSourceService.save(dataSourceVO));
    }

    @GetMapping
    public ResponseEntity<IPage<DataSourceVO>> listPage(@RequestParam Integer page,@RequestParam Integer size){
        return ResponseEntity.ok(dataSourceService.listPage(page,size));
    }


}
