import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import { Building2, CircleCheck, Clock3, Save, ShieldCheck, XCircle } from "lucide-react";
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
  const [isRequestingVerification, setIsRequestingVerification] = useState(false);

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

  async function requestVerification() {
    setError("");
    setSuccess("");
    setIsRequestingVerification(true);
    try {
      const updated = await organizationApi.requestVerification();
      setProfile(updated);
      setSuccess("Đã gửi yêu cầu xác minh cho quản trị viên.");
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể gửi yêu cầu xác minh");
    } finally {
      setIsRequestingVerification(false);
    }
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
              <Card className="p-5">
                <div className="flex items-center gap-2">
                  <VerificationIcon status={profile.verificationStatus} />
                  <h2 className="font-semibold">Xác minh tổ chức</h2>
                </div>
                <p className="mt-3 text-sm font-semibold">{verificationLabels[profile.verificationStatus]}</p>
                <p className="mt-2 text-sm leading-6 text-muted-foreground">{verificationDescriptions[profile.verificationStatus]}</p>
                {profile.verificationNote ? <p className="mt-3 rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{profile.verificationNote}</p> : null}
                {(profile.verificationStatus === "UNVERIFIED" || profile.verificationStatus === "REJECTED") ? (
                  <Button className="mt-4" fullWidth disabled={isRequestingVerification} icon={<ShieldCheck className="h-4 w-4" />} onClick={() => void requestVerification()}>
                    {isRequestingVerification ? "Đang gửi..." : profile.verificationStatus === "REJECTED" ? "Gửi lại yêu cầu" : "Gửi yêu cầu xác minh"}
                  </Button>
                ) : null}
              </Card>
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

const verificationLabels = {
  UNVERIFIED: "Chưa xác minh",
  PENDING: "Đang chờ duyệt",
  VERIFIED: "Đã xác minh",
  REJECTED: "Yêu cầu bị từ chối",
} as const;

const verificationDescriptions = {
  UNVERIFIED: "Hoàn thiện tên, lĩnh vực, website, logo và mô tả trước khi gửi yêu cầu.",
  PENDING: "Quản trị viên đang kiểm tra hồ sơ và website của tổ chức.",
  VERIFIED: "Badge xác minh đang được hiển thị trên các cơ hội của bạn.",
  REJECTED: "Cập nhật hồ sơ theo lý do bên dưới rồi gửi lại yêu cầu.",
} as const;

function VerificationIcon({ status }: { status: OrganizationProfile["verificationStatus"] }) {
  if (status === "VERIFIED") return <CircleCheck className="h-5 w-5 text-emerald-600" aria-hidden="true" />;
  if (status === "PENDING") return <Clock3 className="h-5 w-5 text-amber-600" aria-hidden="true" />;
  if (status === "REJECTED") return <XCircle className="h-5 w-5 text-red-600" aria-hidden="true" />;
  return <ShieldCheck className="h-5 w-5 text-muted-foreground" aria-hidden="true" />;
}
