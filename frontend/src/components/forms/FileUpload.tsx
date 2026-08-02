import { Upload } from "lucide-react";
import type { ChangeEvent } from "react";
import { useRef, useState } from "react";
import { Button } from "../ui/Button";
import { StatusBanner } from "../common/StatusBanner";

type FileUploadProps = {
  label: string;
  accept: string;
  maxSizeMb: number;
  onUpload: (file: File) => Promise<void>;
};

export function FileUpload({ label, accept, maxSizeMb, onUpload }: FileUploadProps) {
  const inputRef = useRef<HTMLInputElement | null>(null);
  const [error, setError] = useState("");
  const [isUploading, setIsUploading] = useState(false);

  async function handleChange(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    setError("");
    if (!file) {
      return;
    }

    if (file.size > maxSizeMb * 1024 * 1024) {
      setError(`Tệp phải có dung lượng không quá ${maxSizeMb} MB.`);
      event.target.value = "";
      return;
    }

    setIsUploading(true);
    try {
      await onUpload(file);
      event.target.value = "";
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Không thể tải tệp lên");
    } finally {
      setIsUploading(false);
    }
  }

  return (
    <div className="rounded-md border border-dashed border-border bg-white p-4">
      <input ref={inputRef} className="hidden" type="file" accept={accept} onChange={handleChange} />
      <Button
        type="button"
        onClick={() => inputRef.current?.click()}
        disabled={isUploading}
        icon={<Upload className="h-4 w-4" aria-hidden="true" />}
      >
        {isUploading ? "Đang tải lên..." : label}
      </Button>
      {error ? <StatusBanner className="mt-3" variant="error" message={error} /> : null}
    </div>
  );
}
