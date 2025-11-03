import BaseService from "~/services/base.service";
import type { ILoginResponse, IUserInfo } from "~/types/auth";

class Auth extends BaseService {
  constructor() {
    super("http://localhost:8080");
  }

  login(body: any) {
    return this.instance.post<ILoginResponse>("/login", body);
  }

  getUserInformation() {
    return this.instance.get<IUserInfo>("/principal");
  }
}

const AuthService = new Auth();

export default AuthService;
