import { Link } from "react-router-dom";
import { ROUTES } from "../../config/routes";

export function Footer() {
  return <footer className="public-footer"><div><Link className="public-brand" to={ROUTES.home}><span>OB</span><strong>Opportunity Board</strong></Link><p>Nền tảng cơ hội dành cho cộng đồng sinh viên Việt Nam.</p></div><nav><Link to={ROUTES.explore}>Khám phá</Link><Link to={ROUTES.login}>Đăng nhập</Link><Link to={ROUTES.register}>Đăng ký</Link></nav><small>© 2026 Opportunity Board</small></footer>;
}
