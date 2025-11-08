<template>
  <div class="grid grid-cols-[2fr_1fr] -m-10">
    <div class="flex flex-col p-6 pt-0">
      <v-breadcrumbs :items="breadcrumbs" class="ps-0">
        <template v-slot:divider>
          <v-icon icon="mdi-chevron-right" />
        </template>
      </v-breadcrumbs>

      <template v-if="lessonSelecting">
        <div class="grow">
          <preview-source
            :type="lessonSelecting.type"
            :src="lessonSelecting.url"
            class="rounded-sm w-full h-full"
          />
        </div>

        <h2 class="text-xl mt-5">{{ lessonSelecting.title }}</h2>
      </template>
    </div>

    <div>
      <v-list
        :selected="route.query.lessonId"
        mandatory
        height="calc(100vh - 82px)"
        class="overflow-auto position-sticky top-[82px]"
      >
        <v-list-item
          v-for="item in lessonsData"
          :key="item.id"
          :value="item.id"
          active-class="text-blue"
          class="py-3"
          @click="$router.replace(`?lessonId=${item.id}`)"
        >
          <v-list-item-title>
            {{ item.title }}
          </v-list-item-title>

          <v-list-item-subtitle class="mt-2">
            <v-icon
              v-if="item.type === ELessonTypes.VIDEO"
              icon="mdi-play-outline"
            />

            <v-icon
              v-else-if="item.type === ELessonTypes.DOCUMENT"
              icon="mdi-file-pdf-box"
            />

            <v-icon
              v-else-if="item.type === ELessonTypes.QUIZ"
              icon="mdi-text-box-outline"
            />

            5min
          </v-list-item-subtitle>
        </v-list-item>
      </v-list>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { BreadcrumbItem } from "vuetify/lib/components/VBreadcrumbs/VBreadcrumbs.mjs";
import { ELessonTypes, type ILesson } from "~/types/lesson";

definePageMeta({
  middleware: "authorization",
  role: E_ROLES.STUDENT,
});

const route = useRoute();
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
    title: "Learn",
  },
];

const lessonTypeOptions = [
  {
    title: "Video",
    value: ELessonTypes.VIDEO,
    color: "blue",
  },
  {
    title: "PDF",
    value: ELessonTypes.DOCUMENT,
    color: "purple",
  },
  {
    title: "Quiz",
    value: ELessonTypes.QUIZ,
    color: "green",
  },
];

const lessonsData = ref<ILesson[]>(
  Array.from({ length: 10 }, (_, i) => {
    const type = lessonTypeOptions[i % lessonTypeOptions.length]!.value;

    const titles = {
      [ELessonTypes.VIDEO]: `Lesson ${i + 1}: Introduction to JavaScript`,
      [ELessonTypes.DOCUMENT]: `Lesson ${i + 1}: Advanced Concepts`,
      [ELessonTypes.QUIZ]: `Lesson ${i + 1}: Practice Quiz`,
    };

    return {
      id: (i + 1).toString(),
      orderIndex: i + 1,
      title: titles[type as keyof typeof titles],
      type,
      url: type === ELessonTypes.DOCUMENT ? "/demo.pdf" : "/demo.mp4",
      isActive: i % 2 === 0,
    };
  })
);

const lessonSelecting = ref<ILesson>();

watch(
  route,
  () => {
    if (!route.query.lessonId) {
      router.replace("?lessonId=1");
      return;
    }
    lessonSelecting.value = lessonsData.value.find(
      (item) => item.id === route.query.lessonId
    );
  },
  { immediate: true }
);
</script>

<style scoped></style>
