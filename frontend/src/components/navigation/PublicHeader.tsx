import { ArrowRight, Menu, Search } from "lucide-react";
import { Link, NavLink } from "react-router-dom";
import { ROUTES } from "../../config/routes";

export function PublicHeader() {
  return <header className="public-header"><div className="public-header-inner">
    <Link className="public-brand" to={ROUTES.home}><span>OB</span><strong>Opportunity Board</strong></Link>
    <nav className="public-nav-links" aria-label="Điều hướng chính"><NavLink to={ROUTES.home}>Trang chủ</NavLink><NavLink to={ROUTES.explore}>Khám phá</NavLink></nav>
    <label className="public-search"><span className="sr-only">Tìm kiếm cơ hội</span><Search aria-hidden="true" /><input placeholder="Tìm kiếm cơ hội..." /></label>
    <div className="public-actions"><Link to={ROUTES.login}>Đăng nhập</Link><Link to={ROUTES.register}>Tham gia ngay <ArrowRight aria-hidden="true" /></Link><button type="button" aria-label="Mở menu"><Menu aria-hidden="true" /></button></div>
  </div></header>;
}
