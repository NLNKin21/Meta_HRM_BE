package com.metahrms.employee_management.scheduler;

import com.metahrms.employee_management.entity.Contract;
import com.metahrms.employee_management.entity.Department;
import com.metahrms.employee_management.entity.Employee;
import com.metahrms.employee_management.enums.ContractStatus;
import com.metahrms.employee_management.enums.EmployeeStatus;
import com.metahrms.employee_management.enums.RoleInDepartment;
import com.metahrms.employee_management.repository.ContractRepository;
import com.metahrms.employee_management.repository.DepartmentRepository;
import com.metahrms.employee_management.repository.EmployeeRepository;
import com.metahrms.employee_management.service.ContractNotificationHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ContractExpiryNotificationScheduler {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ContractNotificationHelperService contractNotificationHelperService;

    //@Scheduled(cron = "*/10 * * * * *") 
    @Scheduled(cron = "0 0 8 * * ?")
    public void notifyExpiringContracts() {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(30);

        Optional<Department> hrDepartmentOpt =
                departmentRepository.findByDeptNameAndIsDeletedFalse("Phòng Nhân sự");
        if (hrDepartmentOpt.isEmpty()) {
            return;
        }

        Integer hrDeptId = hrDepartmentOpt.get().getId();

        List<Employee> hrHeads =
                employeeRepository.findByDeptIdAndRoleInDeptAndStatusAndIsDeletedFalse(
                        hrDeptId,
                        RoleInDepartment.HEAD,
                        EmployeeStatus.ACTIVE
                );

        if (hrHeads.isEmpty()) {
            return;
        }

        List<Contract> contracts = contractRepository.findContractsExpiringBetween(
                today,
                futureDate,
                ContractStatus.ACTIVE
        );

        for (Contract contract : contracts) {
            if (contract.getEmpId() == null || contract.getEndDate() == null) {
                continue;
            }

            long daysLeft = ChronoUnit.DAYS.between(today, contract.getEndDate());

            if (daysLeft < 0 || daysLeft > 30) {
                continue;
            }

            Optional<Employee> employeeOpt = employeeRepository.findById(contract.getEmpId());
            if (employeeOpt.isEmpty()) {
                continue;
            }

            Employee employee = employeeOpt.get();
            String employeeName = employee.getFullName();
            String endDate = contract.getEndDate().toString();

            for (Employee hrHead : hrHeads) {
                contractNotificationHelperService.notifyHrContractExpiring(
                        hrHead.getId(),
                        employee.getId(),
                        contract.getId(),
                        employeeName,
                        endDate,
                        daysLeft
                );
            }

            contractNotificationHelperService.notifyEmployeeContractExpiring(
                    employee.getId(),
                    contract.getId(),
                    endDate,
                    daysLeft
            );
        }
    }

    //@Scheduled(cron = "*/10 * * * * *")  //10s chạy 1 lần
    @Scheduled(cron = "0 0 8 * * ?")   // chạy 1 ngày 1 lần lúc 8h sáng
    public void notifyExpiredContracts() {
        LocalDate today = LocalDate.now();

        Optional<Department> hrDepartmentOpt =
                departmentRepository.findByDeptNameAndIsDeletedFalse("Phòng Nhân sự");
        if (hrDepartmentOpt.isEmpty()) {
            return;
        }

        Integer hrDeptId = hrDepartmentOpt.get().getId();

        List<Employee> hrHeads =
                employeeRepository.findByDeptIdAndRoleInDeptAndStatusAndIsDeletedFalse(
                        hrDeptId,
                        RoleInDepartment.HEAD,
                        EmployeeStatus.ACTIVE
                );

        if (hrHeads.isEmpty()) {
            return;
        }

        List<Contract> contracts = contractRepository.findExpiredContracts(
                today,
                ContractStatus.ACTIVE
        );

        for (Contract contract : contracts) {
            if (contract.getEmpId() == null || contract.getEndDate() == null) {
                continue;
            }

            Optional<Employee> employeeOpt = employeeRepository.findById(contract.getEmpId());
            if (employeeOpt.isEmpty()) {
                continue;
            }

            Employee employee = employeeOpt.get();
            String employeeName = employee.getFullName();
            String endDate = contract.getEndDate().toString();

            for (Employee hrHead : hrHeads) {
                contractNotificationHelperService.notifyHrContractExpired(
                        hrHead.getId(),
                        employee.getId(),
                        contract.getId(),
                        employeeName,
                        endDate
                );
            }

            contractNotificationHelperService.notifyEmployeeContractExpired(
                    employee.getId(),
                    contract.getId(),
                    endDate
            );
        }
    }
}