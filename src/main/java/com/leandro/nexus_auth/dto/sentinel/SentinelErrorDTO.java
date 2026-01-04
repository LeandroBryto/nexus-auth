package com.leandro.nexus_auth.dto.sentinel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentinelErrorDTO {
    private String serviceName;
    private String errorType;
    private String message;
    private String stackTrace;
    private String user;
    private String path;
    private LocalDateTime timestamp;
    private String environment;
}
