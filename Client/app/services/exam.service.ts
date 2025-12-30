import BaseService from "~/services/base.service";
import type { IQuiz, IQuizFormSubmit } from "~/types/quiz";

class Exam extends BaseService {
  async detail(uuid: string) {
    const res = await this.instance.get<IQuiz>(`/quiz/${uuid}/test`);

    return res.data;
  }

  async submit(uuid: string, body: IQuizFormSubmit) {
    const res = await this.instance.post<IQuiz>(`/quiz/${uuid}/submit`, body);

    return res.data;
  }
}

const ExamService = new Exam();

export default ExamService;
