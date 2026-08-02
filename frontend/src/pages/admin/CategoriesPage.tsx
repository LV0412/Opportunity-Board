import { useEffect, useState } from "react";
import { ROUTES } from "../../config/routes";
import { adminApi } from "../../features/admin/api/adminApi";
import type { Category, Tag } from "../../types/admin";

export function CategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([adminApi.listCategories(), adminApi.listTags()])
      .then(([nextCategories, nextTags]) => {
        setCategories(nextCategories);
        setTags(nextTags);
      })
      .catch((exception) => setError(exception instanceof Error ? exception.message : "Không thể tải dữ liệu phân loại"));
  }, []);

  async function createCategory(form: FormData) {
    const created = await adminApi.createCategory({
      name: String(form.get("categoryName") ?? ""),
      slug: String(form.get("categorySlug") ?? ""),
      description: String(form.get("categoryDescription") ?? ""),
    });
    setCategories((current) => [...current, created]);
  }

  async function createTag(form: FormData) {
    const created = await adminApi.createTag({
      name: String(form.get("tagName") ?? ""),
      slug: String(form.get("tagSlug") ?? ""),
    });
    setTags((current) => [...current, created]);
  }

  async function editCategory(item: Category) {
    const name = window.prompt("Tên danh mục", item.name);
    const slug = window.prompt("Slug", item.slug);
    const description = window.prompt("Mô tả", item.description ?? "");
    if (!name?.trim() || !slug?.trim()) {
      return;
    }
    const updated = await adminApi.updateCategory(item.id, { name: name.trim(), slug: slug.trim(), description: description ?? "" });
    setCategories((current) => current.map((entry) => entry.id === item.id ? updated : entry));
  }

  async function deleteCategory(id: string) {
    await adminApi.deleteCategory(id);
    setCategories((current) => current.filter((item) => item.id !== id));
  }

  async function editTag(item: Tag) {
    const name = window.prompt("Tên thẻ", item.name);
    const slug = window.prompt("Slug", item.slug);
    if (!name?.trim() || !slug?.trim()) {
      return;
    }
    const updated = await adminApi.updateTag(item.id, { name: name.trim(), slug: slug.trim() });
    setTags((current) => current.map((entry) => entry.id === item.id ? updated : entry));
  }

  async function deleteTag(id: string) {
    await adminApi.deleteTag(id);
    setTags((current) => current.filter((item) => item.id !== id));
  }

  return (
    <main className="min-h-screen bg-background text-foreground">
      <section className="mx-auto max-w-6xl px-6 py-10">
        <a className="text-sm font-semibold text-primary" href={ROUTES.adminDashboard}>Về dashboard</a>
        <h1 className="mt-4 text-3xl font-bold">Quản lý danh mục và thẻ</h1>
        <p className="mt-2 text-muted-foreground">Dữ liệu phân loại dùng trong biểu mẫu tạo cơ hội và bộ lọc khám phá.</p>
        {error ? <p className="mt-6 rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p> : null}
        <div className="mt-8 grid gap-6 lg:grid-cols-2">
          <section className="rounded-md border border-border bg-white p-5 shadow-sm">
            <h2 className="text-lg font-semibold">Danh mục</h2>
            <form
              className="mt-4 grid gap-3"
              onSubmit={(event) => {
                event.preventDefault();
                void createCategory(new FormData(event.currentTarget));
                event.currentTarget.reset();
              }}
            >
              <input className="h-10 rounded-md border border-border px-3" name="categoryName" placeholder="Tên danh mục" />
              <input className="h-10 rounded-md border border-border px-3" name="categorySlug" placeholder="Slug" />
              <textarea className="min-h-24 rounded-md border border-border px-3 py-2" name="categoryDescription" placeholder="Mô tả" />
              <button className="rounded-md bg-primary px-4 py-2.5 font-semibold text-primary-foreground" type="submit">Thêm danh mục</button>
            </form>
            <div className="mt-5 space-y-3">
              {categories.map((item) => (
                <article key={item.id} className="rounded-md border border-border p-4">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <p className="font-semibold">{item.name}</p>
                      <p className="mt-1 text-sm text-muted-foreground">{item.slug}</p>
                    </div>
                    <div className="flex gap-2">
                      <button className="rounded-md border border-border px-3 py-1.5 text-sm font-semibold" type="button" onClick={() => void editCategory(item)}>Sửa</button>
                      <button className="rounded-md border border-border px-3 py-1.5 text-sm font-semibold" type="button" onClick={() => void deleteCategory(item.id)}>Xóa</button>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          </section>

          <section className="rounded-md border border-border bg-white p-5 shadow-sm">
            <h2 className="text-lg font-semibold">Thẻ</h2>
            <form
              className="mt-4 grid gap-3"
              onSubmit={(event) => {
                event.preventDefault();
                void createTag(new FormData(event.currentTarget));
                event.currentTarget.reset();
              }}
            >
              <input className="h-10 rounded-md border border-border px-3" name="tagName" placeholder="Tên thẻ" />
              <input className="h-10 rounded-md border border-border px-3" name="tagSlug" placeholder="Slug" />
              <button className="rounded-md bg-primary px-4 py-2.5 font-semibold text-primary-foreground" type="submit">Thêm thẻ</button>
            </form>
            <div className="mt-5 flex flex-wrap gap-2">
              {tags.map((item) => (
                <span key={item.id} className="inline-flex items-center gap-2 rounded-md bg-muted px-3 py-2 text-sm font-semibold">
                  {item.name}
                  <button type="button" onClick={() => void editTag(item)}>Sửa</button>
                  <button type="button" onClick={() => void deleteTag(item.id)}>Xóa</button>
                </span>
              ))}
            </div>
          </section>
        </div>
      </section>
    </main>
  );
}

export default CategoriesPage;
