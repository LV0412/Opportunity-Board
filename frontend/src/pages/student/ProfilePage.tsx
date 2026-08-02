import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { FileText, Save } from "lucide-react";
import { FileUpload } from "../../components/forms/FileUpload";
import { EmptyState } from "../../components/common/EmptyState";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/common/LoadingState";
import { StatusBanner } from "../../components/common/StatusBanner";
import { Button } from "../../components/ui/Button";
import { Card } from "../../components/ui/Card";
import { InputField } from "../../components/ui/InputField";
import { TextareaField } from "../../components/ui/TextareaField";
import { ROUTES } from "../../config/routes";
import { MAX_RESUME_SIZE_MB } from "../../config/constants";
import { studentApi } from "../../features/students/api/studentApi";
import type { StudentProfile } from "../../types/profile";

export function ProfilePage() {
  const [profile, setProfile] = useState<StudentProfile | null>(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    studentApi.getMe().then(setProfile).catch((exception) => {
      setError(exception instanceof Error ? exception.message : "Không thể tải hồ sơ");
    });
  }, []);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setSuccess("");
    setIsSaving(true);

    const form = new FormData(event.currentTarget);
    const skills = String(form.get("skills") ?? "")
      .split(",")
      .map((item) => item.trim())
      .filter(Boolean);

    try {
      const updated = await studentApi.updateMe({
        university: String(form.get("university") ?? ""),
        major: String(form.get("major") ?? ""),
        graduationYear: Number(form.get("graduationYear") || 0) || undefined,
        location: String(form.get("location") ?? ""),
        bio: String(form.get("bio") ?? ""),
        interests: String(form.get("interests") ?? ""),
        skills,
      });
      setProfile(updated);
      setSuccess("Đã cập nhật hồ sơ.");
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể cập nhật hồ sơ");
    } finally {
      setIsSaving(false);
    }
  }

  async function handleResumeUpload(file: File) {
    const resume = await studentApi.uploadResume(file);
    setProfile((current) => current ? { ...current, resumes: [resume, ...current.resumes] } : current);
    setSuccess("Đã upload CV.");
  }

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto max-w-5xl px-6 py-10">
        <a className="text-sm font-semibold text-primary" href={ROUTES.studentDashboard}>Về dashboard</a>
        <h1 className="mt-4 text-3xl font-bold">Hồ sơ sinh viên</h1>
        <p className="mt-2 text-muted-foreground">Cập nhật thông tin để chuẩn bị cho các phase ứng tuyển tiếp theo.</p>

        {error ? <ErrorState className="mt-6" message={error} /> : null}
        {success ? <StatusBanner className="mt-6" variant="success" message={success} /> : null}

        {!profile && !error ? <LoadingState className="mt-8" lines={2} /> : null}

        {profile ? (
          <div className="mt-8 grid gap-6 lg:grid-cols-[1fr_320px]">
            <form className="rounded-md border border-border bg-white p-6 shadow-sm" onSubmit={handleSubmit}>
              <div className="grid gap-4 sm:grid-cols-2">
                <InputField label="Trường" name="university" defaultValue={profile.university ?? ""} />
                <InputField label="Ngành" name="major" defaultValue={profile.major ?? ""} />
                <InputField label="Năm tốt nghiệp" name="graduationYear" type="number" defaultValue={profile.graduationYear?.toString() ?? ""} />
                <InputField label="Địa điểm" name="location" defaultValue={profile.location ?? ""} />
              </div>
              <InputField label="Kỹ năng" name="skills" defaultValue={profile.skills.join(", ")} placeholder="Java, React, UI/UX" />
              <TextareaField className="mt-4" label="Sở thích" name="interests" defaultValue={profile.interests ?? ""} />
              <TextareaField className="mt-4" label="Giới thiệu" name="bio" defaultValue={profile.bio ?? ""} />
              <Button className="mt-5" type="submit" disabled={isSaving} icon={<Save className="h-4 w-4" aria-hidden="true" />}>
                {isSaving ? "Đang lưu..." : "Lưu hồ sơ"}
              </Button>
            </form>

            <aside className="space-y-4">
              <FileUpload label="Tải CV PDF lên" accept="application/pdf" maxSizeMb={MAX_RESUME_SIZE_MB} onUpload={handleResumeUpload} />
              <Card className="p-5">
                <h2 className="font-semibold">CV đã upload</h2>
                <div className="mt-4 space-y-3">
                  {profile.resumes.length ? profile.resumes.map((resume) => (
                    <a key={resume.id} className="flex items-center gap-3 rounded-md border border-border p-3 text-sm" href={resume.fileUrl} target="_blank" rel="noreferrer">
                      <FileText className="h-4 w-4 text-primary" aria-hidden="true" />
                      {resume.fileName}
                    </a>
                  )) : <EmptyState title="Chưa có CV" description="Upload CV PDF để sẵn sàng ứng tuyển nhanh hơn." />}
                </div>
              </Card>
            </aside>
          </div>
        ) : null}
      </section>
    </main>
  );
}

export default ProfilePage;
