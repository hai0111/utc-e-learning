import Cookies from "js-cookie";

export default defineNuxtRouteMiddleware(async (to, from) => {
  const jwt = Cookies.get("jwt");

  if (!jwt && to.name !== "login") return navigateTo("/login");
});
