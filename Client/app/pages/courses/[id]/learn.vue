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
            :current-percent="currentTimeWhenLessonClicked"
            class="rounded-sm w-full h-full"
            @time-update-video="onPlayVideo"
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
import CourseService from "~/services/course.service";
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

const { data: lessonsData, status } = useAsyncData(
  `course/${route.params.id as string}/lessons`,
  () => CourseService.getLessons((route.params.id as string) ?? ""),
  { default: () => [] }
);

const lessonSelecting = ref<ILesson>();

const currentTimeWhenLessonClicked = computed(
  () => lessonSelecting.value?.currentPercent ?? 0
);

const id = ref();
const currentPercent = ref();

const postCurrentTime = () => {
  try {
    CourseService.progress(route.params.id as string, {
      lessonId: route.query.lessonId as string,
      progressPercentage: currentPercent.value,
    });
  } catch (err) {
    console.error(err);
  }
};

const onPlayVideo = (event: Event) => {
  id.value++;
  const { duration, currentTime } = event.target as HTMLVideoElement;
  currentPercent.value = ((currentTime / duration) * 100).toFixed();
  if (id.value < 30) return;
  postCurrentTime();
  id.value = 0;
};

watch(
  () => [route.query.lessonId, status.value],
  () => {
    if (status.value !== "success") return;

    if (!route.query.lessonId) {
      router.replace(`?lessonId=${lessonsData.value[0]?.id}`);
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
