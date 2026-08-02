import type { ReactNode, TextareaHTMLAttributes } from "react";

type TextareaFieldProps = TextareaHTMLAttributes<HTMLTextAreaElement> & {
  label: string;
  error?: string;
  hint?: string;
  rightSlot?: ReactNode;
};

export function TextareaField({ label, error, hint, rightSlot, className = "", id, ...props }: TextareaFieldProps) {
  const textareaId = id ?? props.name ?? label.toLowerCase().replace(/\s+/g, "-");

  return (
    <label className="block text-sm font-medium" htmlFor={textareaId}>
      {label}
      <div className="relative mt-2">
        <textarea
          id={textareaId}
          className={`modern-input min-h-24 w-full rounded-md border px-3 py-2 outline-none transition ${
            error
              ? "border-red-300 bg-red-50 focus:border-red-500 focus:ring-2 focus:ring-red-100"
              : "border-border bg-white focus:border-primary focus:ring-2 focus:ring-primary/20"
          } ${rightSlot ? "pr-10" : ""} ${className}`.trim()}
          {...props}
        />
        {rightSlot ? <span className="pointer-events-none absolute right-3 top-3">{rightSlot}</span> : null}
      </div>
      {error ? <p className="mt-2 text-sm text-red-700">{error}</p> : hint ? <p className="mt-2 text-sm text-muted-foreground">{hint}</p> : null}
    </label>
  );
}
