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
  selectedOptionIds?: string[];
  textAnswer?: null | string;
}

export interface IQuiz {
  quizId: string;
  quizTitle: string;
  quizQuestionsResponses: IQuizQuestionResponse[];
  isAttempted: boolean;
}

export interface IAnswer {
  questionId: string;
  selectedOptionIds: string[];
  textAnswer: string | null;
}

export interface IQuizFormSubmit {
  studentAnswers: IAnswer[];
}
