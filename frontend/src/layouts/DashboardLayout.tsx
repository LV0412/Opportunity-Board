import { Menu } from "lucide-react";
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
          <header className="border-b border-border bg-white/90 px-4 py-4 backdrop-blur sm:px-6 lg:px-8">
            <div className="flex flex-wrap items-start justify-between gap-4">
              <div className="flex items-start gap-3">
                <button
                  className="grid h-10 w-10 place-items-center rounded-md border border-border bg-white lg:hidden"
                  type="button"
                  aria-label="Mo menu"
                  onClick={() => setSidebarOpen(true)}
                >
                  <Menu className="h-4 w-4" aria-hidden="true" />
                </button>
                <div>
                  <p className="text-sm font-semibold uppercase tracking-wide text-primary">Opportunity Board</p>
                  <h1 className="mt-2 text-2xl font-bold sm:text-3xl">{title}</h1>
                  {subtitle ? <p className="mt-1 text-sm text-muted-foreground">{subtitle}</p> : null}
                </div>
              </div>
              {actions ? <div className="flex flex-wrap gap-3">{actions}</div> : null}
            </div>
          </header>

          <section className="px-4 py-6 sm:px-6 lg:px-8">
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
