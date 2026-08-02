import { Bell, Menu, Search } from "lucide-react";
import { useState } from "react";
import type { ReactNode } from "react";
import { Sidebar } from "../components/navigation/Sidebar";
import type { UserRole } from "../types/auth";

type DashboardLayoutProps = {
  role: UserRole;
  title: string;
  subtitle?: string;
  actions?: ReactNode;
  onLogout: () => void;
  children: ReactNode;
  secondary?: ReactNode;
};

export function DashboardLayout({
  role,
  title,
  subtitle,
  actions,
  onLogout,
  children,
  secondary,
}: DashboardLayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <main className="min-h-screen bg-background text-foreground">
      <div className="flex min-h-screen">
        <Sidebar role={role} open={sidebarOpen} onClose={() => setSidebarOpen(false)} onLogout={onLogout} />
        <div className="min-w-0 flex-1">
          <header className="sticky top-0 z-20 border-b border-outline-variant/60 bg-surface-container-lowest/95 px-4 backdrop-blur sm:px-6 lg:px-8">
            <div className="flex min-h-16 items-center justify-between gap-4">
              <div className="flex items-start gap-3">
                <button
                  className="grid h-10 w-10 place-items-center rounded-md border border-border bg-white lg:hidden"
                  type="button"
                  aria-label="Mo menu"
                  onClick={() => setSidebarOpen(true)}
                >
                  <Menu className="h-4 w-4" aria-hidden="true" />
                </button>
                <div className="hidden sm:block">
                  <label className="relative block w-[min(36vw,420px)]">
                    <span className="sr-only">Tìm kiếm</span>
                    <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-on-surface-variant" />
                    <input className="h-9 w-full rounded-md border border-outline-variant bg-surface-container-lowest pl-9 pr-3 text-sm outline-none focus:border-primary" placeholder="Tìm kiếm cơ hội..." />
                  </label>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <button className="grid h-9 w-9 place-items-center rounded-md border border-outline-variant bg-white" aria-label="Thông báo" type="button"><Bell className="h-4 w-4" /></button>
                {actions ? <div className="flex flex-wrap gap-2">{actions}</div> : null}
              </div>
            </div>
          </header>

          <section className="px-4 py-6 sm:px-6 lg:px-8">
            <div className="mb-6">
              <h1 className="text-2xl font-bold tracking-tight sm:text-3xl">{title}</h1>
              {subtitle ? <p className="mt-1 text-sm text-on-surface-variant">{subtitle}</p> : null}
            </div>
            {secondary ? (
              <div className="grid gap-6 xl:grid-cols-[minmax(0,1.4fr)_360px]">
                <div className="min-w-0">{children}</div>
                <div className="min-w-0">{secondary}</div>
              </div>
            ) : (
              <div>{children}</div>
            )}
          </section>
        </div>
      </div>
    </main>
  );
}
