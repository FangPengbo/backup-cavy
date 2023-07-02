package cc.focc.cavy.controller;

import cc.focc.cavy.model.common.ResultResponse;
import cc.focc.cavy.service.CommonService;
import cc.focc.cavy.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {


    @Autowired
    private DashboardService dashboardService;

    @GetMapping()
    public ResponseEntity<ResultResponse> dashboard(){
        return ResponseEntity.ok(ResultResponse.success(dashboardService.dashboard()));
    }


}
