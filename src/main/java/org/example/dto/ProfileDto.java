package org.example.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileDto {
    @Size(min = 2, max = 50, message = "用戶名長度必須在 2 到 50 之間")
    private String username;

    // 為了安全，Email 修改通常需要額外驗證流程，這裡僅允許讀取，如果需要修改，請確保 UserService 中有檢查。
    private String email;
    // 這裡可以根據您的需求添加其他可修改的欄位，例如：
    // private String phone;
    // private String address;
}
