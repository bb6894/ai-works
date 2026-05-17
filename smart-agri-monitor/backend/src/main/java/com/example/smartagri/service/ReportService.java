package com.example.smartagri.service;

import com.alibaba.excel.EasyExcel;
import com.example.smartagri.domain.AlarmRecord;
import com.example.smartagri.domain.SensorData;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final SensorDataService sensorDataService;
    private final AlarmRecordService alarmRecordService;

    public void exportSensorData(HttpServletResponse response, Long deviceId, String metricType, LocalDateTime start, LocalDateTime end) throws Exception {
        List<SensorData> rows = sensorDataService.history(deviceId, metricType, start, end);
        prepare(response, "sensor-data.xlsx");
        EasyExcel.write(response.getOutputStream(), SensorData.class)
                .sheet("传感器数据")
                .doWrite(rows);
    }

    public void exportAlarms(HttpServletResponse response) throws Exception {
        List<AlarmRecord> rows = alarmRecordService.listAll();
        prepare(response, "alarm-records.xlsx");
        EasyExcel.write(response.getOutputStream(), AlarmRecord.class)
                .sheet("告警记录")
                .doWrite(rows);
    }

    private void prepare(HttpServletResponse response, String filename) {
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
    }
}
