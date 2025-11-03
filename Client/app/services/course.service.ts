import { DEFAULT_PAGER } from "~/constants";
import BaseService from "~/services/base.service";
import type { ICourseForm } from "~/types/course";

class Course extends BaseService {
  getList(params = DEFAULT_PAGER) {
    return this.instance.get("/courses", {
      params,
    });
  }

  create(body: ICourseForm) {
    return this.instance.post("/courses/create", body);
  }
}

const CourseService = new Course();

export default CourseService;
