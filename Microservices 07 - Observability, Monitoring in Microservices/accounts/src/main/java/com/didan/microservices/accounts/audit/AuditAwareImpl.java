package com.didan.microservices.accounts.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component("auditAwareImpl") // Đánh dấu đây là Bean của Spring với tên là auditAwareImpl, dùng để inject vào class Application
public class AuditAwareImpl implements AuditorAware<String> { // String: kiểu dữ liệu của createdBy và updatedBy

	@Override // Trả về tên người tạo hoặc cập nhật dữ liệu
	public Optional<String> getCurrentAuditor() { // getCurrentAuditor: lấy thông tin người tạo hoặc cập nhật dữ liệu
		return Optional.of("ACCOUNT_MS"); // Trả về tên người tạo hoặc cập nhật dữ liệu
	}

}
