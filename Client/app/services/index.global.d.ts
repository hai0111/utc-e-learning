import "axios";

declare global {
  interface ApiResponse<T = any> {
    code: number;
    message: string;
    data: T;
  }
}

declare module "axios" {
  export interface AxiosInstance {
    get<T = any>(url: string, config?: any): Promise<ApiResponse<T>>;
    post<T = any>(
      url: string,
      data?: any,
      config?: any
    ): Promise<ApiResponse<T>>;
    put<T = any>(
      url: string,
      data?: any,
      config?: any
    ): Promise<ApiResponse<T>>;
    delete<T = any>(url: string, config?: any): Promise<ApiResponse<T>>;
  }
}
