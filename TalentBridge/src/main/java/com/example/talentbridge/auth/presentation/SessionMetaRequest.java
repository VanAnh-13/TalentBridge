package com.example.talentbridge.auth.presentation;

import com.example.talentbridge.auth.domain.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionMetaRequest {
    private String deviceName;
    private String deviceType;
    private String userAgent;

    public DeviceInfo toDevice() {
        return new DeviceInfo(deviceName, deviceType, userAgent);
    }
}
