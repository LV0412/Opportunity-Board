import { Link } from "react-router-dom";
import { ROUTES } from "../../config/routes";

export function Footer() {
  return (
    <footer className="border-t border-outline-variant/60 bg-surface-container-lowest">
      <div className="mx-auto flex max-w-7xl flex-col gap-3 px-6 py-8 text-sm text-on-surface-variant sm:flex-row sm:items-center sm:justify-between">
        <div><strong className="text-on-surface">UniOPP</strong><span className="ml-2">Cổng cơ hội dành cho sinh viên.</span></div>
        <div className="flex gap-4"><Link to={ROUTES.explore}>Khám phá</Link><Link to={ROUTES.login}>Đăng nhập</Link></div>
      </div>
    </footer>
  );
}
