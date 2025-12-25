export enum ELessonTypes {
  VIDEO = "VIDEO",
  DOCUMENT = "DOCUMENT",
  QUIZ = "QUIZ",
}

export interface ILesson {
  id: string;
  courseId: string;
  title: string;
  url: string;
  type: ELessonTypes;
  isActive: true;
  orderIndex: number;
  createdBy: string;
  createdByName: string;
  updatedBy: string;
  updatedByName: string;
  created_at: string;
  updated_at: string;
  currentPercent?: number;
}

export interface ILessonForm {
  title: string;
  url: string;
  type: ELessonTypes;
  orderIndex: number;
  file: File;
  isActive: boolean;
  orderIndexClient?: number;
}

export interface ILessonBatchUpdateItem {
  id: string;
  orderIndex: number;
  isActive: boolean;
}

export interface ILessonBatchUpdateForm {
  lessons: ILessonBatchUpdateItem[];
}

export interface ILessonProgressBody {
  lessonId: string;
  progressPercentage: number;
}
