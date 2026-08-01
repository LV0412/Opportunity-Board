import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Building2, Save } from "lucide-react";
import { FileUpload } from "../../components/forms/FileUpload";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/common/LoadingState";
import { StatusBanner } from "../../components/common/StatusBanner";
import { Button } from "../../components/ui/Button";
import { Card } from "../../components/ui/Card";
import { InputField } from "../../components/ui/InputField";
import { TextareaField } from "../../components/ui/TextareaField";
import { ROUTES } from "../../config/routes";
import { MAX_LOGO_SIZE_MB } from "../../config/constants";
import { organizationApi } from "../../features/organizations/api/organizationApi";
import type { OrganizationProfile } from "../../types/profile";
import { isUrl } from "../../utils/validators";

export function OrganizationProfilePage() {
  const [profile, setProfile] = useState<OrganizationProfile | null>(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    organizationApi.getMe().then(setProfile).catch((exception) => {
      setError(exception instanceof Error ? exception.message : "Không thể tải hồ sơ tổ chức");
    });
  }, []);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setSuccess("");

    const form = new FormData(event.currentTarget);
    const websiteUrl = String(form.get("websiteUrl") ?? "");
    if (websiteUrl && !isUrl(websiteUrl)) {
      setError("Website cần bắt đầu bằng http:// hoặc https://");
      return;
    }

    setIsSaving(true);
    try {
      const updated = await organizationApi.updateMe({
        organizationName: String(form.get("organizationName") ?? ""),
        industry: String(form.get("industry") ?? ""),
        websiteUrl,
        description: String(form.get("description") ?? ""),
      });
      setProfile(updated);
      setSuccess("Đã cập nhật hồ sơ tổ chức.");
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể cập nhật hồ sơ tổ chức");
    } finally {
      setIsSaving(false);
    }
  }

  async function handleLogoUpload(file: File) {
    const updated = await organizationApi.uploadLogo(file);
    setProfile(updated);
    setSuccess("Đã upload logo.");
  }

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto max-w-5xl px-6 py-10">
        <a className="text-sm font-semibold text-primary" href={ROUTES.organizationDashboard}>Về dashboard</a>
        <h1 className="mt-4 text-3xl font-bold">Hồ sơ tổ chức</h1>
        <p className="mt-2 text-muted-foreground">Cập nhật thông tin để bài đăng cơ hội đáng tin cậy hơn.</p>

        {error ? <ErrorState className="mt-6" message={error} /> : null}
        {success ? <StatusBanner className="mt-6" variant="success" message={success} /> : null}

        {!profile && !error ? <LoadingState className="mt-8" lines={2} /> : null}

        {profile ? (
          <div className="mt-8 grid gap-6 lg:grid-cols-[1fr_320px]">
            <form className="rounded-md border border-border bg-white p-6 shadow-sm" onSubmit={handleSubmit}>
              <InputField label="Tên tổ chức" name="organizationName" defaultValue={profile.organizationName} />
              <div className="mt-4 grid gap-4 sm:grid-cols-2">
                <InputField label="Lĩnh vực" name="industry" defaultValue={profile.industry ?? ""} />
                <InputField
                  label="Website"
                  name="websiteUrl"
                  type="url"
                  defaultValue={profile.websiteUrl ?? ""}
                  hint="Dùng website chính thức của tổ chức."
                />
              </div>
              <TextareaField className="mt-4" label="Mô tả" name="description" defaultValue={profile.description ?? ""} />
              <Button className="mt-5" type="submit" disabled={isSaving} icon={<Save className="h-4 w-4" aria-hidden="true" />}>
                {isSaving ? "Đang lưu..." : "Lưu hồ sơ"}
              </Button>
            </form>

            <aside className="space-y-4">
              <FileUpload label="Upload logo" accept="image/png,image/jpeg,image/webp" maxSizeMb={MAX_LOGO_SIZE_MB} onUpload={handleLogoUpload} />
              <Card className="p-5">
                <h2 className="font-semibold">Logo hiện tại</h2>
                <div className="mt-4 grid aspect-square place-items-center rounded-md border border-border bg-muted">
                  {profile.logoUrl ? (
                    <img className="h-full w-full rounded-md object-cover" src={profile.logoUrl} alt={profile.organizationName} />
                  ) : (
                    <Building2 className="h-10 w-10 text-muted-foreground" aria-hidden="true" />
                  )}
                </div>
              </Card>
            </aside>
          </div>
        ) : null}
      </section>
    </main>
  );
}

export default OrganizationProfilePage;
