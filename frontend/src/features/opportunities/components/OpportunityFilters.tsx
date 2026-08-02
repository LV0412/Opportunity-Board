import { Filter, Search, X } from "lucide-react";
import { useEffect, useState } from "react";
import { adminApi } from "../../admin/api/adminApi";
import type { Category, Tag } from "../../../types/admin";
import type { OpportunitySearchParams, OpportunitySort } from "../../../types/opportunity";

const sortOptions: Array<{ label: string; value: OpportunitySort }> = [
  { label: "Mới nhất", value: "newest" },
  { label: "Deadline gần nhất", value: "deadline" },
  { label: "Phổ biến", value: "popular" },
];

type Props = {
  value: OpportunitySearchParams;
  onChange: (value: OpportunitySearchParams) => void;
  onSubmit: () => void;
  onReset: () => void;
};

export function OpportunityFilters({ value, onChange, onSubmit, onReset }: Props) {
  const [categories, setCategories] = useState<Category[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);

  useEffect(() => {
    Promise.all([adminApi.listPublicCategories(), adminApi.listPublicTags()])
      .then(([nextCategories, nextTags]) => {
        setCategories(nextCategories);
        setTags(nextTags);
      })
      .catch(() => {
        setCategories([]);
        setTags([]);
      });
  }, []);

  function setValue<Key extends keyof OpportunitySearchParams>(key: Key, nextValue: OpportunitySearchParams[Key]) {
    onChange({ ...value, [key]: nextValue, page: 0 });
  }

  return (
    <form
      className="opportunity-filters"
      onSubmit={(event) => {
        event.preventDefault();
        onSubmit();
      }}
    >
      <div className="filter-primary-row">
        <label className="block">
          <span className="text-sm font-semibold">Từ khóa</span>
          <div className="mt-2 flex items-center gap-2 rounded-md border border-border bg-background px-3">
            <Search className="h-4 w-4 text-muted-foreground" aria-hidden="true" />
            <input
              className="h-11 w-full bg-transparent text-sm outline-none"
              value={value.query ?? ""}
              onChange={(event) => setValue("query", event.target.value)}
              placeholder="Tên cơ hội, mô tả, tổ chức..."
            />
          </div>
        </label>

        <label className="block">
          <span className="text-sm font-semibold">Danh mục</span>
          <select
            className="mt-2 h-11 w-full rounded-md border border-border bg-background px-3 text-sm outline-none focus:border-primary"
            value={value.categorySlug ?? ""}
            onChange={(event) => setValue("categorySlug", event.target.value)}
          >
            <option value="">Tất cả</option>
            {categories.map((category) => (
              <option key={category.id} value={category.slug}>{category.name}</option>
            ))}
          </select>
        </label>

        <label className="block">
          <span className="text-sm font-semibold">Sắp xếp</span>
          <select
            className="mt-2 h-11 w-full rounded-md border border-border bg-background px-3 text-sm outline-none focus:border-primary"
            value={value.sort ?? "newest"}
            onChange={(event) => setValue("sort", event.target.value as OpportunitySort)}
          >
            {sortOptions.map((option) => (
              <option key={option.value} value={option.value}>{option.label}</option>
            ))}
          </select>
        </label>

        <div className="flex items-end gap-2">
          <button className="inline-flex h-11 items-center gap-2 rounded-md bg-primary px-4 text-sm font-semibold text-primary-foreground" type="submit">
            <Filter className="h-4 w-4" aria-hidden="true" />
            Lọc
          </button>
          <button
            className="grid h-11 w-11 place-items-center rounded-md border border-border text-muted-foreground transition hover:border-primary hover:text-primary"
            type="button"
            title="Xóa bộ lọc"
            aria-label="Xóa bộ lọc"
            onClick={onReset}
          >
            <X className="h-4 w-4" aria-hidden="true" />
          </button>
        </div>
      </div>

      <div className="filter-secondary-row">
        <label className="block">
          <span className="text-sm font-semibold">Địa điểm</span>
          <input
            className="mt-2 h-11 w-full rounded-md border border-border bg-background px-3 text-sm outline-none focus:border-primary"
            value={value.location ?? ""}
            onChange={(event) => setValue("location", event.target.value)}
            placeholder="TP.HCM, Hà Nội..."
          />
        </label>
        <label className="block">
          <span className="text-sm font-semibold">Tag</span>
          <select
            className="mt-2 h-11 w-full rounded-md border border-border bg-background px-3 text-sm outline-none focus:border-primary"
            value={value.skill ?? ""}
            onChange={(event) => setValue("skill", event.target.value)}
          >
            <option value="">Tất cả</option>
            {tags.map((tag) => (
              <option key={tag.id} value={tag.name}>{tag.name}</option>
            ))}
          </select>
        </label>
        <label className="block">
          <span className="text-sm font-semibold">Lĩnh vực</span>
          <input
            className="mt-2 h-11 w-full rounded-md border border-border bg-background px-3 text-sm outline-none focus:border-primary"
            value={value.field ?? ""}
            onChange={(event) => setValue("field", event.target.value)}
            placeholder="Startup, học bổng..."
          />
        </label>
        <label className="block">
          <span className="text-sm font-semibold">Trước ngày</span>
          <input
            className="mt-2 h-11 w-full rounded-md border border-border bg-background px-3 text-sm outline-none focus:border-primary"
            type="date"
            value={value.deadlineBefore ? value.deadlineBefore.slice(0, 10) : ""}
            onChange={(event) => {
              const nextValue = event.target.value ? `${event.target.value}T23:59:59.000Z` : undefined;
              setValue("deadlineBefore", nextValue);
            }}
          />
        </label>
      </div>

      <label className="mt-4 inline-flex items-center gap-2 text-sm font-semibold">
        <input
          className="h-4 w-4 accent-primary"
          type="checkbox"
          checked={value.remote ?? false}
          onChange={(event) => setValue("remote", event.target.checked ? true : undefined)}
        />
        Chỉ hiển thị cơ hội remote
      </label>
    </form>
  );
}
