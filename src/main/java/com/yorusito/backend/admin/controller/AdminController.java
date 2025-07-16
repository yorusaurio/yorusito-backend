package com.yorusito.backend.admin.controller;

import com.yorusito.backend.admin.dto.DashboardStats;
import com.yorusito.backend.admin.dto.SalesReportResponse;
import com.yorusito.backend.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administración", description = "Panel de administración y reportes")
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/dashboard")
    @Operation(summary = "Obtener estadísticas del dashboard", description = "Obtiene estadísticas generales para el dashboard de administración")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        DashboardStats stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/sales-report")
    @Operation(summary = "Reporte de ventas", description = "Obtiene reporte de ventas por rango de fechas")
    public ResponseEntity<List<SalesReportResponse>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<SalesReportResponse> report = adminService.getSalesReport(fechaInicio, fechaFin);
        return ResponseEntity.ok(report);
    }
}
