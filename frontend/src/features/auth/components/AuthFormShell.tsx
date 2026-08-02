import type { ReactNode } from "react";
import { APP_NAME } from "../../../config/constants";

type AuthFormShellProps = {
  title: string;
  subtitle: string;
  children: ReactNode;
};

export function AuthFormShell({ title, subtitle, children }: AuthFormShellProps) {
  return (
    <main className="flex min-h-screen flex-col bg-surface text-foreground">
      <header className="flex h-20 items-center justify-center border-b border-outline-variant/60">
        <a className="text-2xl font-bold tracking-tight text-primary" href="/">{APP_NAME}</a>
      </header>
      <section className="flex flex-1 items-center justify-center px-4 py-12">
        <div className="w-full max-w-[550px] rounded-md border border-outline-variant bg-white p-8 sm:p-10">
          <div className="text-center">
            <h1 className="text-3xl font-bold tracking-tight">{title}</h1>
            <p className="mt-2 text-sm leading-6 text-on-surface-variant">{subtitle}</p>
          </div>
          <div className="mt-8">{children}</div>
        </div>
      </section>
      <footer className="flex min-h-20 flex-wrap items-center justify-between gap-3 border-t border-outline-variant/60 px-6 text-sm text-on-surface-variant">
        <span>© 2026 {APP_NAME}. Tất cả quyền được bảo lưu.</span>
        <span>Điều khoản dịch vụ · Chính sách bảo mật · Liên hệ</span>
      </footer>
    </main>
  );
}
