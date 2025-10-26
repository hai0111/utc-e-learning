import axios, { type AxiosInstance, type AxiosResponse } from "axios";

class BaseService {
  protected instance: AxiosInstance;

  constructor(baseURL: string = "http://localhost:8080/api") {
    this.instance = axios.create({
      baseURL,
      headers: { "Content-Type": "application/json" },
    });

    this.instance.interceptors.response.use(
      (res: AxiosResponse) => {
        const data = res.data;
        if (!data || typeof data.code !== "number") {
          throw { code: 500, message: "Invalid server response", data: null };
        }
        if (data.code !== 200) {
          throw {
            code: data.code,
            message: data.message,
            data: data.data ?? null,
          };
        }
        return data;
      },
      (err) => {
        throw {
          code: err.response?.status || 500,
          message: err.response?.data?.message || err.message || "Server error",
          data: err.response?.data?.data ?? null,
        };
      }
    );
  }
}

export default BaseService;
