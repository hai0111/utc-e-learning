export default defineNuxtRouteMiddleware((to, from) => {
  const auth = useAuth();
  auth.role = E_ROLES.STUDENT;
  if (!auth.role && to.name !== "login") return navigateTo("/login");
});
