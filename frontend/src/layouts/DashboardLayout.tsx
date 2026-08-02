import { Bell, Menu, Search, Sparkles } from "lucide-react";
import { useState } from "react";
import type { ReactNode } from "react";
import { Sidebar } from "../components/navigation/Sidebar";
import type { UserRole } from "../types/auth";

type DashboardLayoutProps = { role: UserRole; title: string; subtitle?: string; actions?: ReactNode; onLogout: () => void; children: ReactNode; secondary?: ReactNode };

export function DashboardLayout({ role, title, subtitle, actions, onLogout, children, secondary }: DashboardLayoutProps) {
  const [sidebarOpen,setSidebarOpen]=useState(false);
  return <main className="dashboard-shell min-h-screen text-foreground"><div className="flex h-screen overflow-hidden">
    <Sidebar role={role} open={sidebarOpen} onClose={() => setSidebarOpen(false)} onLogout={onLogout} />
    <div className="dashboard-main min-w-0 flex-1">
      <header className="dashboard-header"><div className="dashboard-header-tools"><button className="dashboard-menu-button lg:hidden" type="button" aria-label="Mở menu" onClick={() => setSidebarOpen(true)}><Menu aria-hidden="true" /></button><label className="dashboard-search"><span className="sr-only">Tìm kiếm</span><Search aria-hidden="true" /><input placeholder="Tìm kiếm cơ hội..." /></label></div><div className="dashboard-actions"><button className="dashboard-bell" type="button" aria-label="Thông báo"><Bell aria-hidden="true" /></button>{actions}</div></header>
      <section className="dashboard-content"><div className="dashboard-page-title"><p><Sparkles aria-hidden="true" /> Opportunity Board</p><h1>{title}</h1>{subtitle ? <span>{subtitle}</span> : null}</div>{secondary ? <div className="dashboard-columns"><div className="min-w-0">{children}</div><aside className="dashboard-secondary">{secondary}</aside></div> : children}</section>
    </div>
  </div></main>;
}
