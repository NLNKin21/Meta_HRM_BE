package com.metahrms.employee_management.service;


import org.springframework.web.multipart.MultipartFile;

import com.metahrms.employee_management.entity.Attendance.EmployeeFace;

import java.io.IOException;
import java.util.List;

/**
 * Service interface cho Employee Face operations
 */
public interface EmployeeFaceService {
    
    /**
     * Đăng ký khuôn mặt mới cho nhân viên
     * 
     * @param employeeId ID nhân viên
     * @param imageFile File ảnh upload
     * @param isPrimary Có phải ảnh chính không
     * @return EmployeeFace entity đã save
     */
    EmployeeFace enrollFace(Long employeeId, MultipartFile imageFile, Boolean isPrimary) throws IOException;
    
    /**
     * Đăng ký khuôn mặt từ base64 string
     */
    EmployeeFace enrollFaceFromBase64(Long employeeId, String imageBase64, Boolean isPrimary);
    
    /**
     * Lấy tất cả faces của employee
     */
    List<EmployeeFace> getEmployeeFaces(Long employeeId);
    
    /**
     * Lấy tất cả active faces
     */
    List<EmployeeFace> getActiveFaces(Long employeeId);
    
    /**
     * Lấy face chính của employee
     */
    EmployeeFace getPrimaryFace(Long employeeId);
    
    /**
     * Set một face thành primary
     */
    EmployeeFace setPrimaryFace(Integer faceId);
    
    /**
     * Xóa face
     */
    void deleteFace(Integer faceId);
    
    /**
     * Lấy tất cả embeddings của employee (để verify)
     */
    List<List<Double>> getEmployeeEmbeddings(Long employeeId);
    
    /**
     * Upload ảnh lên Cloudinary và trả về URL
     */
    String uploadImageToCloudinary(MultipartFile file) throws IOException;
    
    /**
     * Upload base64 image lên Cloudinary
     */
    String uploadBase64ToCloudinary(String base64Image, String fileName) throws IOException;
}