import type { EQuestionType } from "./lesson";

export interface IOptionResponse {
  optionId: string;
  optionText: string;
  isCorrect: boolean;
  orderIndex: number;
}

export interface IQuizQuestionResponse {
  questionId: string;
  questionText: string;
  questionType: EQuestionType;
  rawPoint: number;
  orderIndex: number;
  optionsResponseList: IOptionResponse[];
  isTouched?: boolean;
  markAsLater?: boolean;
}

export interface IQuiz {
  quizId: string;
  quizTitle: string;
  quizQuestionsResponses: IQuizQuestionResponse[];
  isAttempted: boolean;
}
