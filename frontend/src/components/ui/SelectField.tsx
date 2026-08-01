import type { ReactNode, SelectHTMLAttributes } from "react";

type SelectOption = {
  value: string;
  label: string;
};

type SelectFieldProps = SelectHTMLAttributes<HTMLSelectElement> & {
  label: string;
  options: SelectOption[];
  error?: string;
  hint?: string;
  rightSlot?: ReactNode;
};

export function SelectField({ label, options, error, hint, rightSlot, className = "", id, ...props }: SelectFieldProps) {
  const selectId = id ?? props.name ?? label.toLowerCase().replace(/\s+/g, "-");

  return (
    <label className="block text-sm font-medium" htmlFor={selectId}>
      {label}
      <div className="relative mt-2">
        <select
          id={selectId}
          className={`w-full rounded-md border px-3 py-2 outline-none transition ${
            error
              ? "border-red-300 bg-red-50 focus:border-red-500 focus:ring-2 focus:ring-red-100"
              : "border-border bg-white focus:border-primary focus:ring-2 focus:ring-primary/20"
          } ${rightSlot ? "pr-10" : ""} ${className}`.trim()}
          {...props}
        >
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
        {rightSlot ? <span className="pointer-events-none absolute inset-y-0 right-3 flex items-center">{rightSlot}</span> : null}
      </div>
      {error ? <p className="mt-2 text-sm text-red-700">{error}</p> : hint ? <p className="mt-2 text-sm text-muted-foreground">{hint}</p> : null}
    </label>
  );
}
