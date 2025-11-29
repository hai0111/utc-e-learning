import { DEFAULT_PAGER } from "~/constants";
import BaseService from "~/services/base.service";
import type { ICourse, ICourseForm } from "~/types/course";
import type { ILesson } from "~/types/lesson";
import type { IStudentNotEnrolled } from "~/types/student";

class Course extends BaseService {
  async getList(params = DEFAULT_PAGER) {
    const res = await this.instance.get<ICourse[]>("/courses", {
      params,
    });

    return res.data;
  }

  async detail(uuid: string) {
    const res = await this.instance.get<ICourse>(`/courses/${uuid}`);

    return res.data;
  }

  async getStudents(uuid: string, params = DEFAULT_PAGER) {
    const res = await this.instance.get<IStudentNotEnrolled[]>(
      `/courses/${uuid}/student-in-course`,
      { params }
    );

    return res.data;
  }

  async getStudentsNotInvited(uuid: string, params = DEFAULT_PAGER) {
    const res = await this.instance.get<IStudentNotEnrolled[]>(
      `/courses/${uuid}/student-not-course`,
      { params }
    );

    return res.data;
  }

  async getStudentsNotEnrolled(uuid: string, params = DEFAULT_PAGER) {
    const res = await this.instance.get<IStudentNotEnrolled[]>(
      `/courses/${uuid}/student-not-course`,
      { params }
    );

    return res.data;
  }

  async enrollStudents(uuid: string, studentIds: string[]) {
    const res = await this.instance.post<IStudentNotEnrolled[]>(
      `/courses/${uuid}/add-student-to-course`,
      null,
      {
        params: {
          studentIds,
        },
      }
    );

    return res.data;
  }

  async removeStudent(courseId: string, studentId: string) {
    const res = await this.instance.post(
      `/courses/${courseId}/remove-student-from-course/${studentId}`
    );

    return res.data;
  }

  create(body: ICourseForm) {
    return this.instance.post("/courses/create", body);
  }

  async getLessons(courseId: string) {
    const res = await this.instance.get<ILesson[]>(
      `/courses/${courseId}/lessons`
    );

    return res.data;
  }
}

const CourseService = new Course();

export default CourseService;
