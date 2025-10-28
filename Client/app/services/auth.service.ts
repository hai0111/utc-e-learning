import BaseService from "~/services/base.service";

class Auth extends BaseService {
  constructor() {
    super("http://localhost:8080");
  }

  login(body: any) {
    return this.instance.post("/login", body);
  }

  getUserInformation(body: any) {
    return this.instance.post("/login", body);
  }
}

const AuthService = new Auth();

export default AuthService;
