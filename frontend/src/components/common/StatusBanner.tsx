type StatusBannerVariant = "success" | "error" | "info";

type StatusBannerProps = {
  variant: StatusBannerVariant;
  message: string;
  className?: string;
};

const variantClassNames: Record<StatusBannerVariant, string> = {
  success: "bg-emerald-50 text-emerald-700",
  error: "bg-red-50 text-red-700",
  info: "bg-sky-50 text-sky-700",
};

export function StatusBanner({ variant, message, className = "" }: StatusBannerProps) {
  return <p className={`rounded-md px-4 py-3 text-sm ${variantClassNames[variant]} ${className}`.trim()}>{message}</p>;
}
