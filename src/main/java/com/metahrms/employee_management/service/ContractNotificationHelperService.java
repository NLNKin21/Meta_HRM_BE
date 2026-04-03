package com.metahrms.employee_management.service;

public interface ContractNotificationHelperService {

    void notifyHrContractExpiring(
            Integer hrId,
            Integer employeeId,
            Integer contractId,
            String employeeName,
            String endDate,
            long daysLeft
    );

    void notifyEmployeeContractExpiring(
            Integer employeeId,
            Integer contractId,
            String endDate,
            long daysLeft
    );

    void notifyHrContractExpired(
            Integer hrId,
            Integer employeeId,
            Integer contractId,
            String employeeName,
            String endDate
    );

    void notifyEmployeeContractExpired(
            Integer employeeId,
            Integer contractId,
            String endDate
    );

    void notifyEmployeeContractRenewed(
            Integer employeeId,
            Integer contractId,
            String newEndDate
    );

    void notifyEmployeeContractCreated(
            Integer employeeId,
            Integer contractId,
            String contractType,
            String startDate,
            String endDate
    );

    void notifyEmployeeContractUpdated(
            Integer employeeId,
            Integer contractId
    );

    void notifyEmployeeContractTerminated(
            Integer employeeId,
            Integer contractId,
            String effectiveDate
    );
}