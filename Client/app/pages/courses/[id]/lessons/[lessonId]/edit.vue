<template>
  <div class="flex flex-col items-center px-3">
    <div class="w-full">
      <v-breadcrumbs :items="breadcrumbs" class="ps-0">
        <template v-slot:divider>
          <v-icon icon="mdi-chevron-right" />
        </template>
      </v-breadcrumbs>

      <v-card class="w-full">
        <v-card-text>
          <lesson-form-control
            v-model="formValues"
            :mode="EFormType.EDIT"
            ref="form"
          />
        </v-card-text>
      </v-card>

      <div class="flex justify-end mt-3">
        <group-btn-form
          @click:cancel="onCancel"
          variant="elevated"
          @click:save="onSave"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { cloneDeep } from "lodash";
import { toast } from "vue3-toastify";
import type { BreadcrumbItem } from "vuetify/lib/components/VBreadcrumbs/VBreadcrumbs.mjs";
import { EFormType } from "~/constants/common";
import { DEFAULT_MESSAGES } from "~/constants/messages";
import CourseService from "~/services/course.service";
import { ELessonTypes, type ILessonForm } from "~/types/lesson";

const route = useRoute();

const { params } = useRoute();

const { data: lessonDataOriginal, refresh } = useAsyncData(
  `course/${params.id as string}/lessons/${params.lessonId as string}`,
  () =>
    CourseService.getLessonDetail(
      params.id as string,
      params.lessonId as string
    ),
  {
    default: () => ({
      isActive: true,
      type: ELessonTypes.VIDEO,
      orderIndex: 0,
    }),
  }
);

const router = useRouter();

const breadcrumbs: BreadcrumbItem[] = [
  {
    title: "Courses",
    to: "/",
  },
  {
    title: "Ứng dụng phần mềm",
    to: `/courses/${route.params.id}`,
  },
  {
    title: "Create lesson",
  },
];

const formValues = ref<Partial<ILessonForm>>({});

watch(
  lessonDataOriginal,
  (val) => {
    formValues.value = cloneDeep(val);
  },
  {
    immediate: true,
  }
);

const onCancel = () => {
  router.push(`/courses/${route.params.id}`);
};

const form = ref();

const isLoading = ref(false);

const onSave = async () => {
  const { valid } = await form.value.validate();

  if (!valid) return;

  if (formValues.value.type !== ELessonTypes.QUIZ) {
    formValues.value.quizzesRequest = undefined;
  }

  isLoading.value = true;

  try {
    await CourseService.editLesson(
      route.params.id as string,
      route.params.lessonId as string,
      formValues.value as ILessonForm
    );
    await navigateTo(`/courses/${route.params.id}`);
    toastSuccess(DEFAULT_MESSAGES.apiSuccess);
  } catch (err) {
    toastError(DEFAULT_MESSAGES.apiError);
  } finally {
    isLoading.value = false;
  }
};
</script>
