package com.leandro.nexus_auth.utils;


import com.leandro.nexus_auth.dto.AnaliseRequestDTO;
import com.leandro.nexus_auth.dto.AnaliseResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sentinel", url = "${app.sentinel.url:https://sistema-sentinel-api.fly.dev/api/v1/sentinel}")
public interface SentinelClient {

    @PostMapping("/analisar")
    AnaliseResponseDTO analisarTrafego(@RequestBody AnaliseRequestDTO request);
}