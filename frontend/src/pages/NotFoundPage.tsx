export function NotFoundPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-background px-6 text-center">
      <div>
        <p className="text-sm font-semibold text-primary">404</p>
        <h1 className="mt-3 text-3xl font-bold">Không tìm thấy trang</h1>
        <p className="mt-3 text-muted-foreground">Đường dẫn này chưa tồn tại trong MVP.</p>
        <a className="mt-6 inline-flex rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground" href="/">
          Về trang chủ
        </a>
      </div>
    </main>
  );
}

export default NotFoundPage;
