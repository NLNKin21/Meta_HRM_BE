package com.metahrms.employee_management.enums;

public enum UploadFolderType {
    AVATARS("avatars"),      // Ảnh đại diện
    DOCUMENTS("documents"),  // Giấy tờ tùy thân
    CONTRACTS("contracts"),  // Hợp đồng
    PAYROLLS("payrolls"),    // Phiếu lương
    REQUESTS("requests");    // File đính kèm khi xin nghỉ (ví dụ giấy khám bệnh)


    private final String folderName;

    UploadFolderType(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }

    @Override
    public String toString() {
        return folderName;
    }
}
