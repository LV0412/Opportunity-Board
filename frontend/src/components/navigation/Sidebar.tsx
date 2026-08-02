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
import { NavLink } from "react-router-dom";
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
    { label: "Khám phá", href: ROUTES.explore, icon: Search },
    { label: "Đã lưu", href: ROUTES.studentSavedOpportunities, icon: Bell },
    { label: "Ứng tuyển", href: ROUTES.studentApplications, icon: ClipboardCheck },
    { label: "Hồ sơ", href: ROUTES.studentProfile, icon: UserCircle2 },
  ],
  ORGANIZATION: [
    { label: "Dashboard", href: ROUTES.organizationDashboard, icon: LayoutDashboard },
    { label: "Cơ hội", href: ROUTES.organizationOpportunities, icon: BriefcaseBusiness },
    { label: "Ứng viên", href: ROUTES.organizationApplicants, icon: Users },
    { label: "Hồ sơ tổ chức", href: ROUTES.organizationProfile, icon: FolderKanban },
  ],
  ADMIN: [
    { label: "Dashboard", href: ROUTES.adminDashboard, icon: LayoutDashboard },
    { label: "Chờ duyệt", href: ROUTES.adminPendingOpportunities, icon: ShieldCheck },
    { label: "Báo cáo", href: ROUTES.adminReports, icon: Siren },
    { label: "Người dùng", href: ROUTES.adminUsers, icon: Users },
    { label: "Danh mục", href: ROUTES.adminCategories, icon: Tags },
  ],
};

const roleLabel: Record<UserRole, string> = {
  STUDENT: "Sinh viên",
  ORGANIZATION: "Tổ chức",
  ADMIN: "Admin",
};

export function Sidebar({ role, open, onClose, onLogout }: SidebarProps) {
  const items = itemsByRole[role];

  return (
    <>
      {open ? <button className="fixed inset-0 z-30 bg-slate-900/30 lg:hidden" type="button" aria-label="Dong menu" onClick={onClose} /> : null}
      <aside
        className={`fixed inset-y-0 left-0 z-40 flex w-60 flex-col border-r border-outline-variant/60 bg-surface-container-low transition-transform lg:sticky lg:top-0 lg:h-screen lg:translate-x-0 ${
          open ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <div className="flex h-16 items-center justify-between border-b border-outline-variant/60 px-4">
          <div>
            <p className="text-base font-bold tracking-tight text-on-surface">UniOPP</p>
            <p className="mt-0.5 inline-flex items-center gap-1.5 text-xs font-medium text-on-surface-variant">
              <RoleIcon role={role} />
              {roleLabel[role]}
            </p>
          </div>
          <button className="grid h-10 w-10 place-items-center rounded-md border border-border lg:hidden" type="button" onClick={onClose}>
            <X className="h-4 w-4" aria-hidden="true" />
          </button>
        </div>

        <nav className="flex-1 space-y-1 px-2 py-3" aria-label="Điều hướng chính">
          {items.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.href}
                className={({ isActive }) => `flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition ${
                  isActive ? "bg-[#d8e3fa] text-on-surface" : "text-on-surface-variant hover:bg-surface-container hover:text-on-surface"
                }`}
                to={item.href}
                onClick={onClose}
              >
                <Icon className="h-4 w-4" aria-hidden="true" />
                {item.label}
              </NavLink>
            );
          })}
        </nav>

        <div className="border-t border-outline-variant/60 px-3 py-3">
          <button className="w-full rounded-md border border-outline-variant bg-surface-container-lowest px-4 py-2 text-sm font-semibold hover:bg-surface-container" type="button" onClick={onLogout}>
            Đăng xuất
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
