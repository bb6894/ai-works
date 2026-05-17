package com.example.smartagri.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.smartagri.common.CurrentUser;
import com.example.smartagri.domain.AlarmRecord;
import com.example.smartagri.dto.AlarmHandleRequest;
import com.example.smartagri.dto.PageResult;
import com.example.smartagri.mapper.AlarmRecordMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmRecordService extends CrudService<AlarmRecord> {
    private final AlarmRecordMapper alarmRecordMapper;

    @Override
    protected BaseMapper<AlarmRecord> mapper() {
        return alarmRecordMapper;
    }

    @Override
    public PageResult<AlarmRecord> page(int page, int size, String keyword) {
        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<AlarmRecord>()
                .orderByDesc(AlarmRecord::getTriggeredAt);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(AlarmRecord::getMessage, keyword)
                    .or()
                    .like(AlarmRecord::getStatus, keyword)
                    .or()
                    .like(AlarmRecord::getMetricType, keyword);
        }
        Page<AlarmRecord> result = alarmRecordMapper.selectPage(Page.of(page, size), wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public AlarmRecord handle(Long id, AlarmHandleRequest request) {
        AlarmRecord record = new AlarmRecord();
        record.setId(id);
        record.setStatus(request.status());
        record.setHandler(CurrentUser.username());
        record.setHandleRemark(request.remark());
        record.setUpdatedAt(LocalDateTime.now());
        if ("resolved".equals(request.status())) {
            record.setHandledAt(LocalDateTime.now());
        }
        alarmRecordMapper.updateById(record);
        return get(id);
    }

    @Override
    protected void setId(AlarmRecord entity, Long id) {
        entity.setId(id);
    }
}
