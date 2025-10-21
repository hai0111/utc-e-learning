import { defineStore } from "pinia";

export enum E_ROLES {
  ADMIN = "admin",
  INSTRUCTOR = "instructor",
  STUDENT = "student",
}

export const useAuth = defineStore("auth", {
  state() {
    return {
      role: null as null | E_ROLES,
    };
  },
});
