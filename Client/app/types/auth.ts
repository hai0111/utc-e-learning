export interface ILoginBody {
  email: string;
  password: string;
}

export interface ILoginResponse {
  jwt: string;
  id: string;
  name: string;
  email: string;
  role: E_ROLES;
}

export interface IUserInfo {
  uuid: string;
  name: string;
  code: string;
  email: string;
  roleName: E_ROLES;
  isActive: boolean;
  createdBy: string;
  updateBy: string;
}
