import { Menu, Search } from "lucide-react";
import { Link, NavLink } from "react-router-dom";
import { ROUTES } from "../../config/routes";

export function PublicHeader() {
  return (
    <header className="sticky top-0 z-30 border-b border-outline-variant/60 bg-surface-container-lowest/95 backdrop-blur">
      <div className="mx-auto flex h-16 max-w-7xl items-center gap-6 px-4 sm:px-6">
        <Link className="shrink-0 text-lg font-bold tracking-tight text-on-surface" to={ROUTES.home}>UniOPP</Link>
        <nav className="hidden items-center gap-5 text-sm font-medium md:flex" aria-label="Điều hướng chính">
          <NavLink to={ROUTES.home}>Trang chủ</NavLink>
          <NavLink to={ROUTES.explore}>Khám phá</NavLink>
        </nav>
        <label className="relative ml-auto hidden w-full max-w-sm sm:block">
          <span className="sr-only">Tìm kiếm cơ hội</span>
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-on-surface-variant" />
          <input className="h-9 w-full rounded-md border border-outline-variant bg-surface-container-lowest pl-9 pr-3 text-sm outline-none focus:border-primary" placeholder="Tìm kiếm cơ hội..." />
        </label>
        <Link className="hidden rounded-md border border-outline-variant px-3 py-2 text-sm font-semibold sm:block" to={ROUTES.login}>Đăng nhập</Link>
        <Link className="rounded-md bg-primary px-3 py-2 text-sm font-semibold text-primary-foreground" to={ROUTES.register}>Đăng ký</Link>
        <button className="grid h-9 w-9 place-items-center md:hidden" type="button" aria-label="Mở menu"><Menu className="h-5 w-5" /></button>
      </div>
    </header>
  );
}
