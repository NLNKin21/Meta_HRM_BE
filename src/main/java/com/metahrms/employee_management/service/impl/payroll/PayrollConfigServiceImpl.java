package com.metahrms.employee_management.service.impl.payroll;

import com.metahrms.employee_management.dto.request.payroll.UpdateConfigRequest;
import com.metahrms.employee_management.dto.response.payroll.PayrollConfigDTO;
import com.metahrms.employee_management.entity.Payroll.PayrollConfig;
import com.metahrms.employee_management.repository.Payroll.PayrollConfigRepository;
import com.metahrms.employee_management.service.payroll.PayrollConfigService;
import com.metahrms.employee_management.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollConfigServiceImpl implements PayrollConfigService {

    private final PayrollConfigRepository configRepository;

    @Override
    public List<PayrollConfigDTO> getAll() {
        return configRepository.findByIsDeletedFalseAndIsActiveTrue()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<PayrollConfigDTO> getByGroup(String group) {
        return configRepository.findByConfigGroupAndIsDeletedFalse(group.toUpperCase())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public PayrollConfigDTO getByKey(String key) {
        PayrollConfig config = configRepository
                .findByConfigKeyAndIsDeletedFalse(key)
                .orElseThrow(() -> new IllegalArgumentException("Config not found: " + key));
        return toDTO(config);
    }

    @Override
    @Transactional
    public PayrollConfigDTO update(String key, UpdateConfigRequest request) {
        log.info("[PAYROLL-CONFIG] Updating: key={}, value={}", key, request.getConfigValue());

        PayrollConfig config = configRepository
                .findByConfigKeyAndIsDeletedFalse(key)
                .orElseThrow(() -> new IllegalArgumentException("Config not found: " + key));

        if (request.getConfigValue() != null) {
            config.setConfigValue(request.getConfigValue());
        }
        if (request.getDescription() != null) {
            config.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            config.setIsActive(request.getIsActive());
        }
        config.setUpdatedBy(SecurityUtils.getCurrentUserId());

        return toDTO(configRepository.save(config));
    }

    @Override
    public BigDecimal getValue(String key) {
        return configRepository.findByConfigKeyAndIsDeletedFalse(key)
                .map(PayrollConfig::getConfigValue)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public Map<String, BigDecimal> getAllAsMap() {
        return configRepository.findByIsDeletedFalseAndIsActiveTrue()
                .stream()
                .collect(Collectors.toMap(
                        PayrollConfig::getConfigKey,
                        PayrollConfig::getConfigValue
                ));
    }

    private PayrollConfigDTO toDTO(PayrollConfig c) {
        return PayrollConfigDTO.builder()
                .id(c.getId())
                .configKey(c.getConfigKey())
                .configValue(c.getConfigValue())
                .configGroup(c.getConfigGroup())
                .description(c.getDescription())
                .isActive(c.getIsActive())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}