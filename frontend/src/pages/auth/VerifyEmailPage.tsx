import { useEffect, useState } from "react";
import { CheckCircle2, XCircle } from "lucide-react";
import { StatusBanner } from "../../components/common/StatusBanner";
import { ROUTES } from "../../config/routes";
import { authApi } from "../../features/auth/api/authApi";
import { AuthFormShell } from "../../features/auth/components/AuthFormShell";

type VerifyState = "loading" | "success" | "error";

export function VerifyEmailPage() {
  const [state, setState] = useState<VerifyState>("loading");
  const [message, setMessage] = useState("Đang xác thực email...");

  useEffect(() => {
    const token = new URLSearchParams(window.location.search).get("token");
    if (!token) {
      setState("error");
      setMessage("Đường dẫn xác thực không hợp lệ hoặc thiếu token.");
      return;
    }

    authApi
      .verifyEmail(token)
      .then(() => {
        setState("success");
        setMessage("Email đã được xác thực. Bạn có thể đăng nhập bằng tài khoản vừa đăng ký.");
      })
      .catch((exception) => {
        setState("error");
        setMessage(exception instanceof Error ? exception.message : "Không thể xác thực email.");
      });
  }, []);

  const icon = state === "success"
    ? <CheckCircle2 className="h-5 w-5" aria-hidden="true" />
    : <XCircle className="h-5 w-5" aria-hidden="true" />;

  return (
    <AuthFormShell title="Xác thực email" subtitle="Hoàn tất bước xác thực để kích hoạt tài khoản Opportunity Board.">
      <div className="space-y-4">
        <StatusBanner variant={state === "error" ? "error" : state === "success" ? "success" : "info"} message={message} />
        <a
          className="inline-flex w-full items-center justify-center gap-2 rounded-md bg-primary px-4 py-2.5 font-semibold text-primary-foreground transition hover:opacity-95"
          href={ROUTES.login}
        >
          {icon}
          Đến trang đăng nhập
        </a>
      </div>
    </AuthFormShell>
  );
}

export default VerifyEmailPage;
