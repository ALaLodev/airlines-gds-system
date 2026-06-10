package com.gds.airline.agency_service.controller;

import com.gds.airline.agency_service.dto.AgencyMetricsResponse;
import com.gds.airline.agency_service.dto.AgencyRequest;
import com.gds.airline.agency_service.dto.AgencyResponse;
import com.gds.airline.agency_service.dto.PaginatedResponse;
import com.gds.airline.agency_service.service.AgencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import java.util.List;

@RestController
@RequestMapping("/api/agencies")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @Value("${jwt.secret}")
    private String secretKey;

    @GetMapping("/test-jwt")
    public ResponseEntity<String> testJwt(@RequestParam String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return ResponseEntity.ok("Success! Username: " + claims.getSubject() + ", Role: " + claims.get("role"));
        } catch (Exception e) {
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed: " + e.toString() + "\n" + sw.toString());
        }
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<AgencyResponse>> getAgencies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String region) {
        return ResponseEntity.ok(agencyService.getAgencies(page, size, search, status, region));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgencyResponse> getAgencyById(@PathVariable Long id) {
        return ResponseEntity.ok(agencyService.getAgencyById(id));
    }

    @PostMapping
    public ResponseEntity<AgencyResponse> createAgency(@RequestBody AgencyRequest request) {
        return new ResponseEntity<>(agencyService.createAgency(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgencyResponse> updateAgency(@PathVariable Long id, @RequestBody AgencyRequest request) {
        return ResponseEntity.ok(agencyService.updateAgency(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgency(@PathVariable Long id) {
        agencyService.deleteAgency(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/metrics")
    public ResponseEntity<AgencyMetricsResponse> getMetrics() {
        return ResponseEntity.ok(agencyService.getMetrics());
    }

    @GetMapping("/top-performers")
    public ResponseEntity<List<AgencyResponse>> getTopPerformers() {
        return ResponseEntity.ok(agencyService.getTopPerformers());
    }
}
