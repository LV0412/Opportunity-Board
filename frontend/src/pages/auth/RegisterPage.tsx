import { useState } from "react";
import type { FormEvent } from "react";
import { UserPlus } from "lucide-react";
import { FormErrorSummary } from "../../components/forms/FormErrorSummary";
import { Button } from "../../components/ui/Button";
import { InputField } from "../../components/ui/InputField";
import { SelectField } from "../../components/ui/SelectField";
import { ROUTES } from "../../config/routes";
import { AuthFormShell } from "../../features/auth/components/AuthFormShell";
import { useAuth } from "../../features/auth/hooks/useAuth";
import type { UserRole } from "../../types/auth";
import { isEmail, minLengthMessage, requiredMessage } from "../../utils/validators";

export function RegisterPage() {
  const { register } = useAuth();
  const [role, setRole] = useState<UserRole>("STUDENT");
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [university, setUniversity] = useState("");
  const [major, setMajor] = useState("");
  const [organizationName, setOrganizationName] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");

    const validationErrors = [
      requiredMessage("Họ tên", fullName),
      requiredMessage("Email", email),
      email && !isEmail(email) ? "Email không đúng định dạng." : "",
      requiredMessage("Mật khẩu", password),
      password ? minLengthMessage("Mật khẩu", password, 8) : "",
      role === "STUDENT" && university.trim() === "" ? "Trường là bắt buộc cho tài khoản sinh viên." : "",
      role === "ORGANIZATION" && requiredMessage("Tên tổ chức", organizationName),
    ].filter((message): message is string => Boolean(message));

    if (validationErrors.length) {
      setError(validationErrors[0]);
      return;
    }

    setIsSubmitting(true);
    try {
      await register({
        email,
        password,
        fullName,
        role,
        organizationName,
        university,
        major,
      });
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể đăng ký");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthFormShell title="Đăng ký" subtitle="Tạo tài khoản theo vai trò của bạn trong hệ thống.">
      <form className="space-y-4" onSubmit={handleSubmit}>
        <InputField label="Họ tên" name="fullName" value={fullName} onChange={(event) => setFullName(event.target.value)} required />
        <InputField
          label="Email"
          name="email"
          type="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          error={email && !isEmail(email) ? "Email không đúng định dạng." : undefined}
          required
        />
        <InputField
          label="Mật khẩu"
          name="password"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          hint="Tối thiểu 8 ký tự."
          required
        />
        <SelectField
          label="Vai trò"
          name="role"
          value={role}
          onChange={(event) => setRole(event.target.value as UserRole)}
          options={[
            { value: "STUDENT", label: "Sinh viên" },
            { value: "ORGANIZATION", label: "Tổ chức" },
            { value: "ADMIN", label: "Admin" },
          ]}
        />
        {role === "STUDENT" ? (
          <div className="grid gap-4 sm:grid-cols-2">
            <InputField label="Trường" name="university" value={university} onChange={(event) => setUniversity(event.target.value)} required />
            <InputField label="Ngành" name="major" value={major} onChange={(event) => setMajor(event.target.value)} />
          </div>
        ) : null}
        {role === "ORGANIZATION" ? (
          <InputField label="Tên tổ chức" name="organizationName" value={organizationName} onChange={(event) => setOrganizationName(event.target.value)} required />
        ) : null}
        <FormErrorSummary errors={error ? [error] : []} />
        <Button type="submit" disabled={isSubmitting} fullWidth icon={<UserPlus className="h-4 w-4" aria-hidden="true" />}>
          {isSubmitting ? "Đang tạo tài khoản..." : "Tạo tài khoản"}
        </Button>
      </form>
      <p className="mt-4 text-center text-sm text-muted-foreground">
        Đã có tài khoản?{" "}
        <a className="font-semibold text-primary" href={ROUTES.login}>
          Đăng nhập
        </a>
      </p>
    </AuthFormShell>
  );
}
