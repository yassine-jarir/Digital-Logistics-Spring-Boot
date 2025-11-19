package com.logistic.digitale_logistic.service.Admin;

import com.logistic.digitale_logistic.dto.WareHouseDTO;
import com.logistic.digitale_logistic.entity.Warehouse;
import com.logistic.digitale_logistic.mapper.wareHouseMapper;
import com.logistic.digitale_logistic.repository.WareHouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class wareHouseService {

    private final WareHouseRepository warehouseRepository;
    private final wareHouseMapper warehouseMapper;

    public wareHouseService(WareHouseRepository warehouseRepository, wareHouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    public WareHouseDTO create(WareHouseDTO dto) {
        if (warehouseRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Warehouse with name '" + dto.getName() + "' already exists");
        }
        Warehouse entity = warehouseMapper.toEntity(dto);
        Warehouse saved = warehouseRepository.save(entity);
        return warehouseMapper.toDto(saved);
    }

    public List<WareHouseDTO> getAll() {
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toDto)
                .collect(Collectors.toList());
    }

    public WareHouseDTO getById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return warehouseMapper.toDto(warehouse);
    }

    public WareHouseDTO update(Long id, WareHouseDTO dto) {
        Warehouse existing = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        existing.setName(dto.getName());
        existing.setLocation(dto.getLocation());
        existing.setActive(dto.getActive());

        Warehouse updated = warehouseRepository.save(existing);
        return warehouseMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new RuntimeException("Warehouse not found");
        }
        warehouseRepository.deleteById(id);
    }
}
