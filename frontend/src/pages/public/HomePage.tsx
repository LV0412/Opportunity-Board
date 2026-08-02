import { Bookmark, BriefcaseBusiness, CalendarClock, Filter, Search, ShieldCheck } from "lucide-react";
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Footer } from "../../components/navigation/Footer";
import { PublicHeader } from "../../components/navigation/PublicHeader";
import { ROUTES } from "../../config/routes";
import { opportunityApi } from "../../features/opportunities/api/opportunityApi";
import { OpportunityCard } from "../../features/opportunities/components/OpportunityCard";
import type { Opportunity } from "../../types/opportunity";

export function HomePage() {
  const navigate = useNavigate();
  const [query, setQuery] = useState("");
  const [opportunities, setOpportunities] = useState<Opportunity[]>([]);
  const [mostFollowedOpportunities, setMostFollowedOpportunities] = useState<Opportunity[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      opportunityApi.listApproved(),
      opportunityApi.search({ sort: "popular", page: 0, size: 2 }),
    ])
      .then(([latest, popular]) => {
        setOpportunities(latest.content.slice(0, 6));
        setMostFollowedOpportunities(popular.content);
      })
      .catch(() => {
        setOpportunities([]);
        setMostFollowedOpportunities([]);
      })
      .finally(() => setLoading(false));
  }, []);

  function search() {
    navigate(query.trim() ? `${ROUTES.explore}?query=${encodeURIComponent(query.trim())}` : ROUTES.explore);
  }

  return (
    <main className="min-h-screen bg-surface text-on-surface">
      <PublicHeader />

      <section className="border-b border-outline-variant/60 bg-[radial-gradient(circle_at_85%_25%,rgba(17,128,138,.08),transparent_28%)]">
        <div className="mx-auto grid min-h-[370px] max-w-7xl items-center gap-12 px-4 py-14 sm:px-6 lg:grid-cols-[1fr_.9fr]">
          <div>
            <span className="inline-flex rounded-full border border-primary/20 bg-primary/5 px-3 py-1 text-xs font-semibold text-primary">Dành riêng cho sinh viên Việt Nam</span>
            <h1 className="mt-6 max-w-2xl text-4xl font-bold leading-tight tracking-[-.03em] sm:text-5xl">Cơ hội phù hợp <span className="text-primary">không nên bị bỏ lỡ</span></h1>
            <p className="mt-5 max-w-xl text-base leading-7 text-on-surface-variant">Nền tảng tập hợp và kiểm duyệt hàng ngàn thực tập, học bổng, cuộc thi và hackathon chất lượng cao. Giúp bạn định hướng sự nghiệp ngay từ khi còn trên ghế nhà trường.</p>
            <form className="mt-7 flex max-w-xl gap-2 rounded-md border border-outline-variant bg-white p-1.5" onSubmit={(event) => { event.preventDefault(); search(); }}>
              <label className="relative min-w-0 flex-1"><span className="sr-only">Tìm kiếm cơ hội</span><Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-on-surface-variant" /><input className="h-10 w-full border-0 bg-transparent pl-9 pr-3 text-sm outline-none" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Tìm theo tên cơ hội, tổ chức hoặc kỹ năng" /></label>
              <button className="rounded-md bg-primary px-5 text-sm font-semibold text-white hover:bg-primary/90 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary" type="submit">Tìm kiếm</button>
            </form>
          </div>

          <div className="rotate-1 rounded-lg border border-outline-variant bg-white p-4 shadow-[0_8px_8px_rgba(25,28,30,.08)]" aria-label="Xem trước tiến độ cơ hội">
            <div className="flex items-center justify-between border-b border-border pb-3"><strong className="text-sm">Cơ hội được theo dõi nhiều nhất</strong><span aria-hidden="true">•••</span></div>
            {mostFollowedOpportunities.map((item, index) => <div className="mt-3 flex items-center gap-3 rounded-md border border-border bg-surface px-3 py-2" key={item.id}><span className="grid h-8 w-8 place-items-center rounded bg-primary/10 text-xs font-bold text-primary">{index + 1}</span><div className="min-w-0 flex-1"><p className="truncate text-sm font-semibold">{item.title}</p><p className="truncate text-xs text-on-surface-variant">{item.organizationName}</p></div><span className="whitespace-nowrap text-xs font-semibold text-primary">{item.bookmarkCount} lượt theo dõi</span></div>)}
            {!loading && !mostFollowedOpportunities.length && <p className="mt-3 rounded-md border border-dashed border-outline-variant px-3 py-6 text-center text-sm text-on-surface-variant">Chưa có cơ hội nào được theo dõi.</p>}
            <p className="mt-4 text-xs text-on-surface-variant">Dữ liệu được cập nhật từ lượt lưu trên hệ thống</p>
          </div>
        </div>
      </section>

      <section className="border-b border-outline-variant/60 bg-white"><div className="mx-auto grid max-w-7xl divide-y divide-outline-variant/60 px-4 sm:grid-cols-3 sm:divide-x sm:divide-y-0 sm:px-6">{[[ShieldCheck,"Cơ hội đã được kiểm duyệt"],[Filter,"Tìm kiếm và lọc nhanh"],[CalendarClock,"Theo dõi deadline và trạng thái ứng tuyển"]].map(([Icon,label]) => { const I=Icon as typeof Search; return <div className="flex items-center justify-center gap-3 py-4 text-sm text-on-surface-variant" key={label as string}><I className="h-4 w-4 text-primary" />{label as string}</div>; })}</div></section>

      <section className="mx-auto max-w-7xl px-4 py-16 sm:px-6">
        <div className="flex flex-wrap items-end justify-between gap-5"><div><h2 className="text-3xl font-bold tracking-tight">Cơ hội nổi bật</h2><p className="mt-2 text-sm text-on-surface-variant">Những lựa chọn mới nhất được chúng tôi đề xuất.</p></div><Link className="rounded-md bg-primary px-4 py-2 text-sm font-semibold text-white" to={ROUTES.explore}>Tất cả cơ hội</Link></div>
        {loading ? <div className="mt-7 grid gap-4 md:grid-cols-2 lg:grid-cols-3">{Array.from({length:6}).map((_,i)=><div className="h-64 animate-pulse rounded-md border border-border bg-white" key={i}/>)}</div> : opportunities.length ? <div className="mt-7 grid gap-4 md:grid-cols-2 lg:grid-cols-3">{opportunities.map((item)=><OpportunityCard key={item.id} opportunity={item}/>)}</div> : <div className="mt-7 border border-dashed border-outline-variant bg-white px-6 py-14 text-center"><BriefcaseBusiness className="mx-auto h-8 w-8 text-primary"/><h3 className="mt-4 font-semibold">Chưa có cơ hội nổi bật</h3><p className="mt-2 text-sm text-on-surface-variant">Các cơ hội đã được duyệt sẽ xuất hiện tại đây.</p></div>}
        <div className="mt-8 text-center"><Link className="inline-flex rounded-md border border-primary px-5 py-2.5 text-sm font-semibold text-primary" to={ROUTES.explore}>Xem tất cả cơ hội</Link></div>
      </section>

      <section className="relative overflow-hidden bg-primary py-16 text-white"><div className="absolute -bottom-20 -left-10 h-40 w-40 rounded-full bg-white/5"/><div className="absolute -right-10 -top-20 h-40 w-40 rounded-full bg-white/5"/><div className="relative mx-auto max-w-3xl px-6 text-center"><Bookmark className="mx-auto h-7 w-7"/><h2 className="mt-4 text-3xl font-bold">Sẵn sàng bắt đầu hành trình sự nghiệp?</h2><p className="mx-auto mt-3 max-w-2xl text-sm leading-6 text-white/80">Tạo hồ sơ để nhận thông báo về những cơ hội mới nhất và quản lý lộ trình phát triển của bạn.</p><div className="mt-7 flex flex-wrap justify-center gap-3"><Link className="rounded-md bg-white px-5 py-3 text-sm font-semibold text-primary" to={ROUTES.register}>Tạo hồ sơ sinh viên ngay</Link><Link className="rounded-md border border-white/50 px-5 py-3 text-sm font-semibold" to={ROUTES.explore}>Khám phá các tổ chức</Link></div></div></section>
      <Footer />
    </main>
  );
}

export default HomePage;
