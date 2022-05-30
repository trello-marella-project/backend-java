package com.marella.controllers;

import com.marella.models.Report;
import com.marella.models.User;
import com.marella.payload.request.ApplyReportRequest;
import com.marella.payload.response.ReportsResponse;
import com.marella.repositories.ReportRepository;
import com.marella.services.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.MediaType.APPLICATION_JSON;

@AllArgsConstructor
@RestController
@RequestMapping("/api/reports")
public class ReportsController {
    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    private ReportRepository reportRepository;

    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getAllReports(){
        try{
            return ResponseEntity.ok()
                    .contentType(APPLICATION_JSON)
                    .body(new ReportsResponse(reportRepository.findAll()).toString());
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> applyReport(@PathVariable Long id,
                                         @RequestBody ApplyReportRequest applyReportRequest){
        try{
            // TODO: Я хуй знает, что делает этот маршрут, лиш предположу
            Report report = reportRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException(String.format("report with id: %d does not exist", id))
            );
            switch (applyReportRequest.getStatus()){
                case "ACCEPTED": userService.updateBlockedStatus(report.getAccusedUser(), true);
                case "REJECTED":
                    reportRepository.delete(report);
                    break;
                default:
                    throw new IllegalArgumentException("bad conditions");
            }
            return ResponseEntity.ok()
                    .contentType(APPLICATION_JSON)
                    .body("{\"status\":\"success\"}");
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(APPLICATION_JSON)
                    .body(String.format("{\"status\":\"Error: %s\"}", e.getMessage()));
        }
    }
}
