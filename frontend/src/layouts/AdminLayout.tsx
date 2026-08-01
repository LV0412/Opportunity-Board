import type { ReactNode } from "react";
import { DashboardLayout } from "./DashboardLayout";

type AdminLayoutProps = {
  title: string;
  subtitle?: string;
  actions?: ReactNode;
  onLogout: () => void;
  children: ReactNode;
};

export function AdminLayout({ title, subtitle, actions, onLogout, children }: AdminLayoutProps) {
  return (
    <DashboardLayout role="ADMIN" title={title} subtitle={subtitle} actions={actions} onLogout={onLogout}>
      {children}
    </DashboardLayout>
  );
}
