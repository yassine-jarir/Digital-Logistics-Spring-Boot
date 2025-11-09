package com.logistic.digitale_logistic.controller.Admin;

import com.logistic.digitale_logistic.dto.WareHouseDTO;
import com.logistic.digitale_logistic.service.Admin.wareHouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/warehouses")
public class wareHouseController {

    private final wareHouseService warehouseService;

    public wareHouseController(wareHouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    public WareHouseDTO create(@RequestBody WareHouseDTO dto) {
        return warehouseService.create(dto);
    }

    @GetMapping
    public List<WareHouseDTO> getAll() {
        return warehouseService.getAll();
    }

    @GetMapping("/{id}")
    public WareHouseDTO getById(@PathVariable Long id) {
        return warehouseService.getById(id);
    }

    @PutMapping("/{id}")
    public WareHouseDTO update(@PathVariable Long id, @RequestBody WareHouseDTO dto) {
        return warehouseService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        warehouseService.delete(id);
    }
}
