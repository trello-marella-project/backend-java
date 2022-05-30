package com.marella.payload.response;

import com.marella.models.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReportsResponse {
    private List<Report> reports;

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();
        String prefix = "{\"reports\":[";
        if(reports.isEmpty()) response.append(prefix);
        for(Report report : reports){
            response.append(prefix);
            response.append(report.toString());
            prefix = ",";
        }
        return response.append("]}").toString();
    }
}
