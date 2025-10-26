import { isEmpty } from "lodash";

export const required =
  (message: string = "This field is required") =>
  (val: any) =>
    !isEmpty(val) || message;
