package com.gds.airline.booking_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {

    @GetMapping("/api/auth/users/{id}/email")
    String getUserEmailById(@PathVariable("id") Long id);
}
