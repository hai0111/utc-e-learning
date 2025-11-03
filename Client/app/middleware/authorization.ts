export default defineNuxtRouteMiddleware((to, from) => {
  const requiredRole = to.meta.role;
  if (!requiredRole) return;

  const auth = useAuth();

  if (auth.userInfo?.roleName !== requiredRole)
    throw createError({
      statusCode: 404,
      message: "Page not found",
    });
});
