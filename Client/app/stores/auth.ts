import { defineStore } from "pinia";
import { toast } from "vue3-toastify";
import AuthService from "~/services/auth.service";
import type { IUserInfo } from "~/types/auth";

export enum E_ROLES {
  ADMIN = "ADMIN",
  INSTRUCTOR = "INSTRUCTOR",
  STUDENT = "STUDENT",
}

export const useAuth = defineStore("auth", {
  state() {
    return {
      userInfo: null as null | IUserInfo,
    };
  },

  actions: {
    async getUserInfo() {
      try {
        const res = await AuthService.getUserInformation();
        this.userInfo = res.data;
      } catch {
        await this.logout();

        toast("Invalid login session", {
          type: "error",
        });
      }
    },

    async logout(backPath?: string) {
      this.userInfo = null;
      clearAllCookies();
      await navigateTo(`/login${backPath ? `?back=${backPath}` : ""}`);
    },
  },
});
