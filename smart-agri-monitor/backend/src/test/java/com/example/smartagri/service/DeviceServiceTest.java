package com.example.smartagri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.smartagri.domain.Device;
import com.example.smartagri.mapper.DeviceMapper;
import org.junit.jupiter.api.Test;

class DeviceServiceTest {

    @Test
    void createSetsAuditFieldsAndDefaultOnlineStatus() {
        DeviceMapper deviceMapper = mock(DeviceMapper.class);
        DeviceService service = new DeviceService(deviceMapper);
        Device device = new Device();
        device.setDeviceCode("D-001");
        device.setDeviceName("温湿度传感器");

        Device created = service.create(device);

        assertThat(created.getCreatedAt()).isNotNull();
        assertThat(created.getUpdatedAt()).isNotNull();
        assertThat(created.getOnlineStatus()).isEqualTo(1);
        verify(deviceMapper).insert(device);
    }

    @Test
    void updateKeepsIdAndReloadsPersistedDevice() {
        DeviceMapper deviceMapper = mock(DeviceMapper.class);
        Device persisted = new Device();
        persisted.setId(12L);
        persisted.setDeviceName("土壤墒情传感器");
        when(deviceMapper.selectById(12L)).thenReturn(persisted);
        DeviceService service = new DeviceService(deviceMapper);

        Device update = new Device();
        update.setDeviceName("土壤墒情传感器");
        Device result = service.update(12L, update);

        assertThat(update.getId()).isEqualTo(12L);
        assertThat(update.getUpdatedAt()).isNotNull();
        assertThat(result).isSameAs(persisted);
        verify(deviceMapper).updateById(update);
    }

    @Test
    void markSeenMarksDeviceOnline() {
        DeviceMapper deviceMapper = mock(DeviceMapper.class);
        DeviceService service = new DeviceService(deviceMapper);

        service.markSeen(5L);

        verify(deviceMapper).updateById(any(Device.class));
    }
}
