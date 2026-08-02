import { Bell, BriefcaseBusiness, ClipboardCheck, FolderKanban, GraduationCap, LayoutDashboard, LogOut, Search, ShieldCheck, Siren, Tags, UserCircle2, Users, X } from "lucide-react";
import type { LucideIcon } from "lucide-react";
import type { ReactNode } from "react";
import { Link, NavLink } from "react-router-dom";
import { ROUTES } from "../../config/routes";
import type { UserRole } from "../../types/auth";

type SidebarProps = { role: UserRole; open: boolean; onClose: () => void; onLogout: () => void };
type NavItem = { label: string; href: string; icon: LucideIcon };

const itemsByRole: Record<UserRole, NavItem[]> = {
  STUDENT: [
    { label: "Tổng quan", href: ROUTES.studentDashboard, icon: LayoutDashboard },
    { label: "Khám phá", href: ROUTES.explore, icon: Search },
    { label: "Đã lưu", href: ROUTES.studentSavedOpportunities, icon: Bell },
    { label: "Ứng tuyển", href: ROUTES.studentApplications, icon: ClipboardCheck },
    { label: "Hồ sơ", href: ROUTES.studentProfile, icon: UserCircle2 },
  ],
  ORGANIZATION: [
    { label: "Tổng quan", href: ROUTES.organizationDashboard, icon: LayoutDashboard },
    { label: "Cơ hội", href: ROUTES.organizationOpportunities, icon: BriefcaseBusiness },
    { label: "Ứng viên", href: ROUTES.organizationApplicants, icon: Users },
    { label: "Hồ sơ tổ chức", href: ROUTES.organizationProfile, icon: FolderKanban },
  ],
  ADMIN: [
    { label: "Tổng quan", href: ROUTES.adminDashboard, icon: LayoutDashboard },
    { label: "Chờ duyệt", href: ROUTES.adminPendingOpportunities, icon: ShieldCheck },
    { label: "Báo cáo", href: ROUTES.adminReports, icon: Siren },
    { label: "Xác minh tổ chức", href: ROUTES.adminOrganizationVerifications, icon: ShieldCheck },
    { label: "Người dùng", href: ROUTES.adminUsers, icon: Users },
    { label: "Danh mục", href: ROUTES.adminCategories, icon: Tags },
  ],
};

const roleLabel: Record<UserRole, string> = { STUDENT: "Sinh viên", ORGANIZATION: "Tổ chức", ADMIN: "Quản trị viên" };

export function Sidebar({ role, open, onClose, onLogout }: SidebarProps) {
  return <>
    {open ? <button className="fixed inset-0 z-30 bg-slate-950/35 backdrop-blur-sm lg:hidden" type="button" aria-label="Đóng menu" onClick={onClose} /> : null}
    <aside className={`modern-sidebar fixed inset-y-0 left-0 z-40 flex w-72 flex-col transition-transform lg:translate-x-0 ${open ? "translate-x-0" : "-translate-x-full"}`}>
      <div className="sidebar-brand-row"><Link className="sidebar-brand" to={ROUTES.home} onClick={onClose}><span>OB</span><div><strong>Opportunity</strong><small>Board</small></div></Link><button className="sidebar-close lg:hidden" type="button" aria-label="Đóng menu" onClick={onClose}><X aria-hidden="true" /></button></div>
      <div className="sidebar-role"><RoleIcon role={role} /><div><small>Không gian làm việc</small><strong>{roleLabel[role]}</strong></div></div>
      <nav className="sidebar-nav" aria-label="Điều hướng chính"><p>MENU CHÍNH</p>{itemsByRole[role].map((item) => { const Icon=item.icon; return <NavLink key={item.href} className={({isActive}) => isActive ? "is-active" : ""} to={item.href} onClick={onClose}><Icon aria-hidden="true" /><span>{item.label}</span><i /></NavLink>; })}</nav>
      <div className="sidebar-tip"><span>✨</span><strong>Mẹo nhỏ</strong><p>Hoàn thiện thông tin để khai thác hiệu quả hơn các tính năng.</p></div>
      <button className="sidebar-logout" type="button" onClick={onLogout}><LogOut aria-hidden="true" /> Đăng xuất</button>
    </aside>
  </>;
}

function RoleIcon({ role }: { role: UserRole }) {
  const byRole: Record<UserRole, ReactNode> = { STUDENT:<GraduationCap aria-hidden="true" />, ORGANIZATION:<BriefcaseBusiness aria-hidden="true" />, ADMIN:<ShieldCheck aria-hidden="true" /> };
  return byRole[role];
}
