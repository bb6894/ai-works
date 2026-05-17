package com.example.smartagri.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartagri.domain.Device;
import com.example.smartagri.mapper.DeviceMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService extends CrudService<Device> {
    private final DeviceMapper deviceMapper;

    @Override
    protected BaseMapper<Device> mapper() {
        return deviceMapper;
    }

    @Override
    protected LambdaQueryWrapper<Device> query(String keyword) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<Device>()
                .orderByDesc(Device::getId);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Device::getDeviceName, keyword)
                    .or()
                    .like(Device::getDeviceCode, keyword)
                    .or()
                    .like(Device::getDeviceType, keyword);
        }
        return wrapper;
    }

    @Override
    public Device create(Device entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        if (entity.getOnlineStatus() == null) {
            entity.setOnlineStatus(1);
        }
        return super.create(entity);
    }

    @Override
    public Device update(Long id, Device entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        return super.update(id, entity);
    }

    public void markSeen(Long id) {
        Device device = new Device();
        device.setId(id);
        device.setOnlineStatus(1);
        device.setLastSeenAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        deviceMapper.updateById(device);
    }

    @Override
    protected void setId(Device entity, Long id) {
        entity.setId(id);
    }
}
