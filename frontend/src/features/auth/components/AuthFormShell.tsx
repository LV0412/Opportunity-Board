import type { ReactNode } from "react";
import { APP_NAME } from "../../../config/constants";

type AuthFormShellProps = {
  title: string;
  subtitle: string;
  children: ReactNode;
};

export function AuthFormShell({ title, subtitle, children }: AuthFormShellProps) {
  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto grid min-h-screen max-w-6xl items-center gap-10 px-6 py-12 lg:grid-cols-[1fr_420px]">
        <div>
          <p className="text-sm font-semibold uppercase tracking-wide text-primary">{APP_NAME}</p>
          <h1 className="mt-4 max-w-2xl text-4xl font-bold tracking-normal sm:text-5xl">
            Tập trung cơ hội học tập, nghề nghiệp và khởi nghiệp cho sinh viên
          </h1>
          <p className="mt-5 max-w-xl text-base leading-7 text-muted-foreground">
            Đăng nhập để lưu cơ hội, theo dõi hồ sơ ứng tuyển và nhận dashboard phù hợp với vai trò của bạn.
          </p>
        </div>

        <div className="rounded-md border border-border bg-white p-6 shadow-sm">
          <h2 className="text-2xl font-semibold">{title}</h2>
          <p className="mt-2 text-sm leading-6 text-muted-foreground">{subtitle}</p>
          <div className="mt-6">{children}</div>
        </div>
      </section>
    </main>
  );
}
