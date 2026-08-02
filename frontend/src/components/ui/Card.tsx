import type { HTMLAttributes, ReactNode } from "react";

type CardProps = HTMLAttributes<HTMLDivElement> & {
  title?: string;
  description?: string;
  actions?: ReactNode;
};

export function Card({ title, description, actions, className = "", children, ...props }: CardProps) {
  return (
    <section className={`app-card rounded-md border border-border bg-white p-5 shadow-sm ${className}`.trim()} {...props}>
      {title || description || actions ? (
        <div className="flex items-start justify-between gap-3">
          <div>
            {title ? <h2 className="text-lg font-semibold">{title}</h2> : null}
            {description ? <p className="mt-1 text-sm text-muted-foreground">{description}</p> : null}
          </div>
          {actions}
        </div>
      ) : null}
      {children ? <div className={title || description || actions ? "mt-5" : ""}>{children}</div> : null}
    </section>
  );
}
