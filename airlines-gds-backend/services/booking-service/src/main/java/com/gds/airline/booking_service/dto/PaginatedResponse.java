package com.gds.airline.booking_service.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> data,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast
) {}
