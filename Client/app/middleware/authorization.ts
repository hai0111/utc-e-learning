export default defineNuxtRouteMiddleware((to, from) => {
  const requiredRole = to.meta.role;
  if (!requiredRole) return;
  const auth = useAuth();
  if (auth.role !== requiredRole)
    throw createError({
      statusCode: 404,
      message: "Không tìm thấy trang",
    });
});
