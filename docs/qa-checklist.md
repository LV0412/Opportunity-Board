# QA Checklist - Opportunity Board

## 1. Mục tiêu

Checklist này dùng để kiểm tra lại các flow chính của hệ thống trước khi demo, nghiệm thu hoặc release nội bộ.

## 2. Môi trường kiểm tra

- Backend chạy thành công và kết nối database.
- Frontend chạy được ở môi trường local hoặc staging.
- Có sẵn 3 tài khoản: `student`, `organization`, `admin`.
- Có dữ liệu seed cho category, tag và ít nhất 1 opportunity đã được duyệt.

## 3. Student Flow

### A. Đăng ký và đăng nhập

- [ ] Đăng ký tài khoản `student` thành công.
- [ ] Không thể đăng ký trùng email.
- [ ] Đăng nhập đúng email/mật khẩu thành công.
- [ ] Đăng nhập sai mật khẩu hiển thị lỗi dễ hiểu.

### B. Hồ sơ sinh viên

- [ ] Mở trang hồ sơ không bị vỡ layout trên mobile và desktop.
- [ ] Cập nhật trường, ngành, địa điểm, kỹ năng thành công.
- [ ] Upload CV PDF hợp lệ thành công.
- [ ] Upload file vượt dung lượng bị chặn và báo lỗi rõ ràng.

### C. Tìm kiếm và lưu cơ hội

- [ ] Tìm cơ hội theo từ khóa trả kết quả đúng.
- [ ] Lọc theo category hoạt động đúng.
- [ ] Lọc theo remote hoạt động đúng.
- [ ] Trạng thái loading, empty state, error state hiển thị đúng.
- [ ] Bookmark một opportunity thành công.
- [ ] Bookmark lại cùng opportunity không tạo trùng dữ liệu.
- [ ] Bỏ bookmark thành công.

### D. Ứng tuyển

- [ ] Mở trang chi tiết opportunity đã duyệt thành công.
- [ ] Ứng tuyển với cover letter thành công.
- [ ] Không thể ứng tuyển 2 lần cùng một opportunity.
- [ ] Không thể ứng tuyển opportunity đã hết hạn.
- [ ] Theo dõi trạng thái application trong dashboard sinh viên thành công.
- [ ] Khi organization đổi trạng thái application, student thấy notification mới.

## 4. Organization Flow

### A. Đăng ký và hồ sơ tổ chức

- [ ] Đăng ký tài khoản `organization` thành công.
- [ ] Cập nhật tên tổ chức, lĩnh vực, website, mô tả thành công.
- [ ] Upload logo hợp lệ thành công.

### B. Quản lý opportunity

- [ ] Tạo opportunity mới thành công.
- [ ] Opportunity mới tạo có trạng thái `PENDING`.
- [ ] Sửa opportunity của chính mình thành công.
- [ ] Không thể sửa opportunity của organization khác.
- [ ] Đóng opportunity của chính mình thành công.
- [ ] Không thể đóng opportunity của organization khác.

### C. Ứng viên

- [ ] Chỉ thấy application thuộc opportunity của chính organization.
- [ ] Xem CV ứng viên thành công khi có file.
- [ ] Cập nhật trạng thái application thành công.
- [ ] Organization khác không thể xem hoặc cập nhật application không thuộc sở hữu.

## 5. Admin Flow

### A. Phân quyền

- [ ] Đăng nhập `admin` thành công.
- [ ] Student truy cập endpoint/trang admin bị chặn.
- [ ] Organization truy cập endpoint/trang admin bị chặn.

### B. Duyệt nội dung

- [ ] Xem danh sách opportunity chờ duyệt thành công.
- [ ] Approve opportunity thành công.
- [ ] Reject opportunity với lý do thành công.
- [ ] Opportunity bị reject không hiển thị public.
- [ ] Opportunity được approve hiển thị ở trang public.

### C. Quản trị hệ thống

- [ ] Xem danh sách user thành công.
- [ ] Khóa/mở user thành công.
- [ ] Xem danh sách report thành công.
- [ ] Cập nhật trạng thái report thành công.
- [ ] Tạo category mới thành công.
- [ ] Không thể tạo category trùng slug.
- [ ] Tạo tag mới thành công.

## 6. Regression Checks

- [ ] Swagger mở được và hiển thị các API chính.
- [ ] Error response validation có format thống nhất.
- [ ] Sau khi approve opportunity, search public vẫn hoạt động đúng.
- [ ] Dashboard của 3 role vẫn tải được dữ liệu chính.
- [ ] Không có lỗi console nghiêm trọng ở frontend trong 3 flow chính.

## 7. Kết quả kiểm tra

| Hạng mục | Người kiểm tra | Kết quả | Ghi chú |
|---|---|---|---|
| Student flow |  |  |  |
| Organization flow |  |  |  |
| Admin flow |  |  |  |
| Regression |  |  |  |
