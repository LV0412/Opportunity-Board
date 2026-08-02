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
import { isEmail, requiredMessage } from "../../utils/validators";

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

    const validationErrors = [
      requiredMessage("Email", email),
      requiredMessage("Mật khẩu", password),
      email && !isEmail(email) ? "Email không đúng định dạng." : "",
    ].filter(Boolean);

    if (validationErrors.length) {
      setError(validationErrors[0]);
      return;
    }

    setIsSubmitting(true);
    try {
      await login({ email, password });
      const requestedPath = (location.state as { from?: string } | null)?.from;
      if (requestedPath?.startsWith("/")) {
        navigate(requestedPath, { replace: true });
      }
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể đăng nhập");
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
          autoComplete="email"
          error={email && !isEmail(email) ? "Email không đúng định dạng." : undefined}
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
