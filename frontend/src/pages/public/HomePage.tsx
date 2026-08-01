import { useEffect, useState } from "react";
import { Search, ShieldCheck, Timer } from "lucide-react";
import { ROUTES } from "../../config/routes";
import { opportunityApi } from "../../features/opportunities/api/opportunityApi";
import { OpportunityCard } from "../../features/opportunities/components/OpportunityCard";
import type { Opportunity } from "../../types/opportunity";

const highlights = [
  {
    icon: Search,
    title: "Khám phá cơ hội",
    description: "Tìm thực tập, học bổng, hackathon và chương trình startup trong một nơi.",
  },
  {
    icon: Timer,
    title: "Không lỡ deadline",
    description: "Theo dõi cơ hội đã lưu và chuẩn bị ứng tuyển đúng hạn.",
  },
  {
    icon: ShieldCheck,
    title: "Nguồn tin kiểm duyệt",
    description: "Bài đăng của tổ chức sẽ được admin duyệt trước khi hiển thị công khai.",
  },
];

export function HomePage() {
  const [opportunities, setOpportunities] = useState<Opportunity[]>([]);

  useEffect(() => {
    opportunityApi.listApproved()
      .then((response) => setOpportunities(response.content))
      .catch(() => setOpportunities([]));
  }, []);

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto flex min-h-[80vh] max-w-6xl flex-col justify-center px-6 py-12">
        <div className="max-w-3xl">
          <p className="text-sm font-semibold uppercase tracking-wide text-primary">
            Opportunity Board MVP
          </p>
          <h1 className="mt-4 text-4xl font-bold tracking-normal text-foreground sm:text-5xl">
            Bảng cơ hội tập trung cho sinh viên đại học
          </h1>
          <p className="mt-5 max-w-2xl text-lg leading-8 text-muted-foreground">
            Tìm kiếm, lưu, ứng tuyển và theo dõi các cơ hội nghề nghiệp, học bổng,
            hackathon và chương trình khởi nghiệp trong một dashboard rõ ràng.
          </p>
          <div className="mt-7 flex flex-wrap gap-3">
            <a className="rounded-md bg-primary px-4 py-2.5 font-semibold text-primary-foreground" href={ROUTES.register}>
              Tạo tài khoản
            </a>
            <a className="rounded-md border border-border bg-white px-4 py-2.5 font-semibold text-foreground" href={ROUTES.explore}>
              Khám phá ngay
            </a>
          </div>
        </div>

        <div className="mt-10 grid gap-4 md:grid-cols-3">
          {highlights.map((item) => (
            <article key={item.title} className="rounded-md border border-border bg-white p-5 shadow-sm">
              <item.icon className="h-6 w-6 text-primary" aria-hidden="true" />
              <h2 className="mt-4 text-lg font-semibold">{item.title}</h2>
              <p className="mt-2 text-sm leading-6 text-muted-foreground">{item.description}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="border-t border-border bg-white">
        <div className="mx-auto max-w-6xl px-6 py-10">
          <div className="flex flex-wrap items-end justify-between gap-4">
            <div>
              <h2 className="text-2xl font-bold">Cơ hội mới được duyệt</h2>
              <p className="mt-2 text-sm text-muted-foreground">Các bài đã qua kiểm duyệt và còn hạn.</p>
            </div>
            <a className="text-sm font-semibold text-primary" href={ROUTES.explore}>Xem tất cả</a>
          </div>
          <div className="mt-6 grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {opportunities.map((item) => (
              <OpportunityCard key={item.id} opportunity={item} />
            ))}
            {!opportunities.length ? <p className="text-sm text-muted-foreground">Chưa có cơ hội public.</p> : null}
          </div>
        </div>
      </section>
    </main>
  );
}

export default HomePage;
