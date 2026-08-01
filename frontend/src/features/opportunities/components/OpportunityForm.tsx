import { useState } from "react";
import type { FormEvent } from "react";
import { Save } from "lucide-react";
import { useEffect } from "react";
import { adminApi } from "../../admin/api/adminApi";
import { FormErrorSummary } from "../../../components/forms/FormErrorSummary";
import { Button } from "../../../components/ui/Button";
import { InputField } from "../../../components/ui/InputField";
import { SelectField } from "../../../components/ui/SelectField";
import { TextareaField } from "../../../components/ui/TextareaField";
import type { Category, Tag } from "../../../types/admin";
import type { Opportunity, OpportunityPayload } from "../../../types/opportunity";
import { isUrl, requiredMessage } from "../../../utils/validators";

type OpportunityFormProps = {
  initialValue?: Opportunity;
  onSubmit: (payload: OpportunityPayload) => Promise<void>;
};

export function OpportunityForm({ initialValue, onSubmit }: OpportunityFormProps) {
  const [categories, setCategories] = useState<Category[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

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

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");

    const form = new FormData(event.currentTarget);
    const title = String(form.get("title") ?? "");
    const description = String(form.get("description") ?? "");
    const applyUrl = String(form.get("applyUrl") ?? "");
    const deadline = String(form.get("deadlineAt") ?? "");
    const selectedTags = form.getAll("tags").map((item) => String(item));
    const validationErrors = [
      requiredMessage("Tiêu đề", title),
      requiredMessage("Mô tả", description),
      applyUrl && !isUrl(applyUrl) ? "Link ứng tuyển cần bắt đầu bằng http:// hoặc https://" : "",
    ].filter(Boolean);

    if (validationErrors.length) {
      setError(validationErrors[0]);
      return;
    }

    setIsSubmitting(true);

    try {
      await onSubmit({
        title,
        description,
        requirements: String(form.get("requirements") ?? ""),
        location: String(form.get("location") ?? ""),
        remote: form.get("remote") === "on",
        applyUrl,
        deadlineAt: deadline ? new Date(deadline).toISOString() : undefined,
        categorySlug: String(form.get("categorySlug") ?? categories[0]?.slug ?? "internship"),
        tags: selectedTags,
      });
      event.currentTarget.reset();
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể lưu cơ hội");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <form className="rounded-md border border-border bg-white p-6 shadow-sm" onSubmit={handleSubmit}>
      <div className="grid gap-4 sm:grid-cols-2">
        <InputField label="Tiêu đề" name="title" defaultValue={initialValue?.title ?? ""} required />
        <SelectField
          label="Danh mục"
          name="categorySlug"
          defaultValue={initialValue?.categorySlug ?? categories[0]?.slug ?? "internship"}
          options={categories.map((category) => ({ value: category.slug, label: category.name }))}
        />
        <InputField label="Địa điểm" name="location" defaultValue={initialValue?.location ?? ""} />
        <InputField label="Link ứng tuyển" name="applyUrl" type="url" defaultValue={initialValue?.applyUrl ?? ""} hint="Dùng link form, ATS hoặc website chính thức." />
        <InputField label="Deadline" name="deadlineAt" type="datetime-local" defaultValue={toDateTimeLocal(initialValue?.deadlineAt)} />
        <label className="block text-sm font-medium">
          Tags
          <select className="mt-2 min-h-28 w-full rounded-md border border-border bg-white px-3 py-2 outline-none focus:border-primary focus:ring-2 focus:ring-primary/20" name="tags" multiple defaultValue={initialValue?.tags ?? []}>
            {tags.map((tag) => <option key={tag.id} value={tag.name}>{tag.name}</option>)}
          </select>
        </label>
      </div>
      <label className="mt-4 flex items-center gap-2 text-sm font-medium">
        <input className="h-4 w-4 accent-primary" name="remote" type="checkbox" defaultChecked={initialValue?.remote ?? false} />
        Remote
      </label>
      <TextareaField className="mt-4" label="Mô tả" name="description" defaultValue={initialValue?.description ?? ""} required />
      <TextareaField className="mt-4" label="Yêu cầu" name="requirements" defaultValue={initialValue?.requirements ?? ""} />
      <FormErrorSummary errors={error ? [error] : []} />
      <Button className="mt-5" type="submit" disabled={isSubmitting} icon={<Save className="h-4 w-4" aria-hidden="true" />}>
        {isSubmitting ? "Đang lưu..." : "Lưu và gửi duyệt"}
      </Button>
    </form>
  );
}

function toDateTimeLocal(value?: string | null) {
  if (!value) {
    return "";
  }
  return new Date(value).toISOString().slice(0, 16);
}
