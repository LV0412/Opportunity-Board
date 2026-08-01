import {
  Bell,
  BriefcaseBusiness,
  ClipboardCheck,
  FolderKanban,
  GraduationCap,
  LayoutDashboard,
  Search,
  ShieldCheck,
  Siren,
  Tags,
  UserCircle2,
  Users,
  X,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";
import type { ReactNode } from "react";
import { ROUTES } from "../../config/routes";
import type { UserRole } from "../../types/auth";

type SidebarProps = {
  role: UserRole;
  open: boolean;
  onClose: () => void;
  onLogout: () => void;
};

type NavItem = {
  label: string;
  href: string;
  icon: LucideIcon;
};

const itemsByRole: Record<UserRole, NavItem[]> = {
  STUDENT: [
    { label: "Dashboard", href: ROUTES.studentDashboard, icon: LayoutDashboard },
    { label: "Kham pha", href: ROUTES.explore, icon: Search },
    { label: "Da luu", href: ROUTES.studentSavedOpportunities, icon: Bell },
    { label: "Ung tuyen", href: ROUTES.studentApplications, icon: ClipboardCheck },
    { label: "Ho so", href: ROUTES.studentProfile, icon: UserCircle2 },
  ],
  ORGANIZATION: [
    { label: "Dashboard", href: ROUTES.organizationDashboard, icon: LayoutDashboard },
    { label: "Co hoi", href: ROUTES.organizationOpportunities, icon: BriefcaseBusiness },
    { label: "Ung vien", href: ROUTES.organizationApplicants, icon: Users },
    { label: "Ho so to chuc", href: ROUTES.organizationProfile, icon: FolderKanban },
  ],
  ADMIN: [
    { label: "Dashboard", href: ROUTES.adminDashboard, icon: LayoutDashboard },
    { label: "Cho duyet", href: ROUTES.adminPendingOpportunities, icon: ShieldCheck },
    { label: "Reports", href: ROUTES.adminReports, icon: Siren },
    { label: "Nguoi dung", href: ROUTES.adminUsers, icon: Users },
    { label: "Categories", href: ROUTES.adminCategories, icon: Tags },
  ],
};

const roleLabel: Record<UserRole, string> = {
  STUDENT: "Sinh vien",
  ORGANIZATION: "To chuc",
  ADMIN: "Admin",
};

export function Sidebar({ role, open, onClose, onLogout }: SidebarProps) {
  const currentPath = window.location.pathname;
  const items = itemsByRole[role];

  return (
    <>
      {open ? <button className="fixed inset-0 z-30 bg-slate-900/30 lg:hidden" type="button" aria-label="Dong menu" onClick={onClose} /> : null}
      <aside
        className={`fixed inset-y-0 left-0 z-40 flex w-72 flex-col border-r border-border bg-white transition-transform lg:static lg:translate-x-0 ${
          open ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <div className="flex items-center justify-between border-b border-border px-5 py-5">
          <div>
            <p className="text-sm font-semibold uppercase tracking-wide text-primary">Opportunity Board</p>
            <p className="mt-2 inline-flex items-center gap-2 text-sm font-medium text-muted-foreground">
              <RoleIcon role={role} />
              {roleLabel[role]}
            </p>
          </div>
          <button className="grid h-10 w-10 place-items-center rounded-md border border-border lg:hidden" type="button" onClick={onClose}>
            <X className="h-4 w-4" aria-hidden="true" />
          </button>
        </div>

        <nav className="flex-1 space-y-1 px-3 py-4">
          {items.map((item) => {
            const active = currentPath === item.href;
            const Icon = item.icon;
            return (
              <a
                key={item.href}
                className={`flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-semibold transition ${
                  active
                    ? "bg-primary text-primary-foreground"
                    : "text-foreground hover:bg-muted"
                }`}
                href={item.href}
                onClick={onClose}
              >
                <Icon className="h-4 w-4" aria-hidden="true" />
                {item.label}
              </a>
            );
          })}
        </nav>

        <div className="border-t border-border px-4 py-4">
          <button className="w-full rounded-md border border-border bg-white px-4 py-2.5 text-sm font-semibold" type="button" onClick={onLogout}>
            Dang xuat
          </button>
        </div>
      </aside>
    </>
  );
}

function RoleIcon({ role }: { role: UserRole }) {
  const byRole: Record<UserRole, ReactNode> = {
    STUDENT: <GraduationCap className="h-4 w-4" aria-hidden="true" />,
    ORGANIZATION: <BriefcaseBusiness className="h-4 w-4" aria-hidden="true" />,
    ADMIN: <ShieldCheck className="h-4 w-4" aria-hidden="true" />,
  };
  return byRole[role];
}
