import BaseService from "~/services/base.service";
import type { IQuiz } from "~/types/quiz";

class Exam extends BaseService {
  async detail(uuid: string) {
    const res = await this.instance.get<IQuiz>(`/quiz/${uuid}/test`);

    return res.data;
  }
}

const ExamService = new Exam();

export default ExamService;
