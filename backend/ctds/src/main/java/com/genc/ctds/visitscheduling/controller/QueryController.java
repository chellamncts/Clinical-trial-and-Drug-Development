package com.genc.ctds.visitscheduling.controller;

import com.genc.ctds.visitscheduling.model.QueryRecord;
import com.genc.ctds.visitscheduling.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queries")
public class QueryController {

    @Autowired
    private QueryService queryService;

    @GetMapping
    public List<QueryRecord> getQueries(@RequestParam(value = "status", required = false) String status) {
        if (status != null && status.equalsIgnoreCase("open")) {
            return queryService.getOpenQueries();
        }
        return queryService.getAllQueries();
    }

    @PostMapping
    public ResponseEntity<QueryRecord> raiseQuery(@RequestBody Map<String, String> body) {
        int visitId = Integer.parseInt(body.get("visitId"));
        String description = body.get("description");
        return ResponseEntity.ok(queryService.raiseQuery(visitId, description));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<QueryRecord> resolveQuery(@PathVariable int id,
                                                    @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(queryService.resolveQuery(id, body.get("resolutionNote")));
    }
}

