export interface ICourseForm {
  title: string;
  description: string;
  isActive: boolean;
}

export interface ICourse {
  id: string;
  description: string;
  isActive: boolean;
  title: string;
  totalStudents: number;
  progressPercentage: number | null;
  updatedAt: string;
  createdAt: string;
  instructor: string;
}
