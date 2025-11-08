export enum ELessonTypes {
  VIDEO = "video",
  DOCUMENT = "document",
  QUIZ = "quiz",
}

export interface ILesson {
  id: string;
  orderIndex: number;
  title: string;
  type: ELessonTypes;
  url: string;
  isActive: boolean;
}

export interface ILessonForm {
  orderIndex: number;
  title: string;
  type: ELessonTypes;
  url: string;
  isActive: boolean;
}
