export const useAuthorization = (role: E_ROLES) => {
  const auth = useAuth();
  if (auth.role !== role) throw createError({ statusCode: 404 });
};
