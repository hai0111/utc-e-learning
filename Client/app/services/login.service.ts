import BaseService from "~/services/base.service";

class Login extends BaseService {
  constructor() {
    super("http://localhost:8080");
  }

  submit(body: any) {
    return this.instance.post("/login", body);
  }
}

const LoginService = new Login();

export default LoginService;
