import { useEffect, useMemo, useState } from "react";
import type { FormEvent } from "react";
import { BookOpen, CheckCircle2, FileText, GraduationCap, MapPin, Save, Sparkles, UserRound } from "lucide-react";
import { FileUpload } from "../../components/forms/FileUpload";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/common/LoadingState";
import { StatusBanner } from "../../components/common/StatusBanner";
import { Button } from "../../components/ui/Button";
import { InputField } from "../../components/ui/InputField";
import { TextareaField } from "../../components/ui/TextareaField";
import { MAX_RESUME_SIZE_MB } from "../../config/constants";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { studentApi } from "../../features/students/api/studentApi";
import { DashboardLayout } from "../../layouts/DashboardLayout";
import type { StudentProfile } from "../../types/profile";

export function ProfilePage() {
  const { logout } = useAuth();
  const [profile, setProfile] = useState<StudentProfile | null>(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    studentApi.getMe().then(setProfile).catch((exception) => {
      setError(exception instanceof Error ? exception.message : "Không thể tải hồ sơ");
    });
  }, []);

  const completion = useMemo(() => {
    if (!profile) return 0;
    const fields = [profile.university, profile.major, profile.graduationYear, profile.location, profile.bio, profile.interests, profile.skills.length, profile.resumes.length];
    return Math.round((fields.filter(Boolean).length / fields.length) * 100);
  }, [profile]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setSuccess("");
    setIsSaving(true);
    const form = new FormData(event.currentTarget);
    const skills = String(form.get("skills") ?? "").split(",").map((item) => item.trim()).filter(Boolean);

    try {
      const updated = await studentApi.updateMe({
        university: String(form.get("university") ?? ""), major: String(form.get("major") ?? ""),
        graduationYear: Number(form.get("graduationYear") || 0) || undefined, location: String(form.get("location") ?? ""),
        bio: String(form.get("bio") ?? ""), interests: String(form.get("interests") ?? ""), skills,
      });
      setProfile(updated);
      setSuccess("Hồ sơ của bạn đã được cập nhật.");
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể cập nhật hồ sơ");
    } finally { setIsSaving(false); }
  }

  async function handleResumeUpload(file: File) {
    const resume = await studentApi.uploadResume(file);
    setProfile((current) => current ? { ...current, resumes: [resume, ...current.resumes] } : current);
    setSuccess("CV đã được tải lên thành công.");
  }

  return (
    <DashboardLayout role="STUDENT" title="Hồ sơ của bạn" subtitle="Xây dựng hồ sơ nổi bật để tiếp cận những cơ hội phù hợp hơn." onLogout={logout}>
      {error ? <ErrorState className="profile-alert" message={error} /> : null}
      {success ? <StatusBanner className="profile-alert" variant="success" message={success} /> : null}
      {!profile && !error ? <LoadingState className="profile-loading" lines={4} /> : null}

      {profile ? (
        <div className="profile-workspace">
          <section className="profile-overview-card">
            <div className="profile-avatar">{profile.fullName.split(" ").map((part) => part[0]).slice(-2).join("").toUpperCase()}</div>
            <div className="profile-identity"><span>HỒ SƠ SINH VIÊN</span><h2>{profile.fullName}</h2><p>{profile.email}</p></div>
            <div className="profile-completion"><div><span>Mức độ hoàn thiện</span><strong>{completion}%</strong></div><div className="profile-progress"><i style={{ width: `${completion}%` }} /></div><p>{completion === 100 ? "Hồ sơ đã sẵn sàng ứng tuyển." : "Bổ sung các mục còn thiếu để tăng chất lượng đề xuất."}</p></div>
          </section>

          <div className="profile-layout">
            <form className="profile-form" onSubmit={handleSubmit}>
              <section className="profile-form-section">
                <SectionHeading icon={GraduationCap} title="Học vấn" description="Thông tin giúp hệ thống hiểu định hướng chuyên môn của bạn." />
                <div className="profile-field-grid">
                  <InputField label="Trường đại học" name="university" defaultValue={profile.university ?? ""} placeholder="Ví dụ: Đại học FPT" />
                  <InputField label="Chuyên ngành" name="major" defaultValue={profile.major ?? ""} placeholder="Ví dụ: Kỹ thuật phần mềm" />
                  <InputField label="Năm tốt nghiệp" name="graduationYear" type="number" min="2000" max="2100" defaultValue={profile.graduationYear?.toString() ?? ""} />
                  <InputField label="Địa điểm" name="location" defaultValue={profile.location ?? ""} placeholder="Thành phố hiện tại" />
                </div>
              </section>

              <section className="profile-form-section">
                <SectionHeading icon={Sparkles} title="Năng lực và sở thích" description="Phân tách kỹ năng bằng dấu phẩy để nhận gợi ý chính xác hơn." />
                <InputField label="Kỹ năng" name="skills" defaultValue={profile.skills.join(", ")} placeholder="Java, React, UI/UX" hint="Ví dụ: Java, Spring Boot, React" />
                <TextareaField label="Lĩnh vực quan tâm" name="interests" rows={3} defaultValue={profile.interests ?? ""} placeholder="Bạn muốn khám phá lĩnh vực hoặc loại cơ hội nào?" />
              </section>

              <section className="profile-form-section">
                <SectionHeading icon={UserRound} title="Giới thiệu bản thân" description="Một đoạn giới thiệu ngắn giúp tổ chức hiểu rõ điểm mạnh của bạn." />
                <TextareaField label="Tóm tắt hồ sơ" name="bio" rows={5} defaultValue={profile.bio ?? ""} placeholder="Mục tiêu, kinh nghiệm và điều bạn đang tìm kiếm..." />
              </section>

              <div className="profile-save-bar"><p>Mọi thay đổi chỉ được ghi nhận sau khi bạn nhấn lưu.</p><Button type="submit" disabled={isSaving} icon={<Save className="h-4 w-4" aria-hidden="true" />}>{isSaving ? "Đang lưu..." : "Lưu thay đổi"}</Button></div>
            </form>

            <aside className="profile-sidebar">
              <section className="profile-resume-upload"><div><FileText aria-hidden="true" /><span><strong>Tải CV mới</strong><small>PDF, tối đa {MAX_RESUME_SIZE_MB} MB</small></span></div><FileUpload label="Chọn tệp PDF" accept="application/pdf" maxSizeMb={MAX_RESUME_SIZE_MB} onUpload={handleResumeUpload} /></section>
              <section className="profile-resume-library"><header><div><BookOpen aria-hidden="true" /><span><h2>CV của bạn</h2><p>{profile.resumes.length} tài liệu</p></span></div></header>
                <div className="profile-resume-list">{profile.resumes.length ? profile.resumes.map((resume, index) => (
                  <a key={resume.id} href={resume.fileUrl} target="_blank" rel="noreferrer"><span><FileText aria-hidden="true" /></span><div><strong>{resume.fileName}</strong><small>{resume.primaryResume ? "CV chính" : `CV số ${index + 1}`}</small></div>{resume.primaryResume ? <CheckCircle2 aria-label="CV chính" /> : null}</a>
                )) : <div className="profile-resume-empty"><FileText aria-hidden="true" /><h3>Chưa có CV</h3><p>Tải CV để ứng tuyển nhanh hơn.</p></div>}</div>
              </section>
              <section className="profile-tip-card"><MapPin aria-hidden="true" /><div><strong>Mẹo nhỏ</strong><p>Hãy dùng tên kỹ năng cụ thể và mô tả thành tích có số liệu để hồ sơ nổi bật hơn.</p></div></section>
            </aside>
          </div>
        </div>
      ) : null}
    </DashboardLayout>
  );
}

function SectionHeading({ icon: Icon, title, description }: { icon: typeof GraduationCap; title: string; description: string }) {
  return <header className="profile-section-heading"><span><Icon aria-hidden="true" /></span><div><h2>{title}</h2><p>{description}</p></div></header>;
}

export default ProfilePage;
