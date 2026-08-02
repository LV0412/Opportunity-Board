import { useState } from "react";
import type { FormEvent } from "react";
import { LogIn } from "lucide-react";
import { useLocation, useNavigate } from "react-router-dom";
import { FormErrorSummary } from "../../components/forms/FormErrorSummary";
import { Button } from "../../components/ui/Button";
import { InputField } from "../../components/ui/InputField";
import { ROUTES } from "../../config/routes";
import { AuthFormShell } from "../../features/auth/components/AuthFormShell";
import { useAuth } from "../../features/auth/hooks/useAuth";
import { emailTypoMessage, isEmail, normalizeEmail, requiredMessage } from "../../utils/validators";

export function LoginPage() {
  const { login } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");

    const normalizedEmail = normalizeEmail(email);
    const typoError = emailTypoMessage(normalizedEmail);

    const validationErrors = [
      requiredMessage("Email", normalizedEmail),
      requiredMessage("Mật khẩu", password),
      typoError,
      normalizedEmail && !isEmail(normalizedEmail) ? "Email không đúng định dạng." : "",
    ].filter(Boolean);

    if (validationErrors.length) {
      setError(validationErrors[0]);
      return;
    }

    setIsSubmitting(true);
    try {
      setEmail(normalizedEmail);
      await login({ email: normalizedEmail, password });
      const requestedPath = (location.state as { from?: string } | null)?.from;
      if (requestedPath?.startsWith("/")) {
        navigate(requestedPath, { replace: true });
      }
    } catch (exception) {
      const message = exception instanceof Error ? exception.message : "Không thể đăng nhập";
      setError(message === "Please verify your email before logging in"
        ? "Tài khoản chưa được kích hoạt. Vui lòng mở email xác thực đã được gửi khi đăng ký."
        : message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthFormShell title="Đăng nhập" subtitle="Tiếp tục vào dashboard Opportunity Board của bạn.">
      <form className="space-y-4" onSubmit={handleSubmit}>
        <InputField
          label="Email"
          type="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          onBlur={() => setEmail(normalizeEmail(email))}
          autoComplete="email"
          error={emailTypoMessage(email) || (email && !isEmail(email) ? "Email không đúng định dạng." : undefined)}
          required
        />
        <InputField
          label="Mật khẩu"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          autoComplete="current-password"
          required
        />
        <FormErrorSummary errors={error ? [error] : []} />
        <Button type="submit" disabled={isSubmitting} fullWidth icon={<LogIn className="h-4 w-4" aria-hidden="true" />}>
          {isSubmitting ? "Đang đăng nhập..." : "Đăng nhập"}
        </Button>
      </form>
      <p className="mt-4 text-center text-sm text-muted-foreground">
        Chưa có tài khoản?{" "}
        <a className="font-semibold text-primary" href={ROUTES.register}>
          Đăng ký
        </a>
      </p>
    </AuthFormShell>
  );
}

export default LoginPage;
