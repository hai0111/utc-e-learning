<template>
  <div class="flex flex-col items-center px-3 create-lesson">
    <div class="w-full">
      <v-breadcrumbs :items="breadcrumbs" class="ps-0">
        <template v-slot:divider>
          <v-icon icon="mdi-chevron-right" />
        </template>
      </v-breadcrumbs>

      <v-card class="w-full">
        <v-card-text>
          <v-form class="grid grid-cols-2 gap-y-3 gap-x-9 mt-5" ref="form">
            <info-item label="Title">
              <v-text-field v-model="formValues.title" :rules="[required()]" />
            </info-item>

            <info-item label="Active">
              <v-switch inset v-model="formValues.isActive" color="primary" />
            </info-item>

            <div class="flex items-center gap-3">
              Choose type:
              <v-btn-toggle
                v-model="formValues.type"
                color="primary"
                density="compact"
              >
                <v-btn :value="ELessonTypes.VIDEO">
                  <v-icon>mdi-video</v-icon>
                </v-btn>

                <v-btn :value="ELessonTypes.DOCUMENT">
                  <v-icon>mdi-file-pdf-box </v-icon>
                </v-btn>

                <!-- <v-btn :value="ELessonTypes.QUIZ">
                  <v-icon>mdi-head-question </v-icon>
                </v-btn> -->
              </v-btn-toggle>
            </div>

            <div class="col-span-2">
              <VFileUpload
                density="comfortable"
                variant="comfortable"
                :filter-by-type="acceptFiles"
              />
            </div>

            <div
              class="col-span-2 grid grid-cols-[auto_1fr] items-center gap-3 mt-6"
            >
              <div>Order Index:</div>

              <v-slider
                v-model="formValues.orderIndex"
                show-ticks="always"
                step="1"
                :min="1"
                :max="lessonsData.length"
                tick-size="4"
                thumb-size="15"
                thumb-label="always"
                hide-details
              />

              <div></div>

              <div class="flex gap-2 justify-between">
                <div>
                  <template v-if="(formValues.orderIndex ?? 1) > 1">
                    Before:
                    <div>
                      <strong> {{ orderIndexDetail.before }} </strong>
                    </div>
                  </template>
                </div>

                <div>
                  <template
                    v-if="(formValues.orderIndex ?? 0) < lessonsData.length"
                  >
                    After:
                    <div>
                      <strong> {{ orderIndexDetail.after }} </strong>
                    </div>
                  </template>
                </div>
              </div>
            </div>
          </v-form>
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
import type { after } from "lodash";
import { toast } from "vue3-toastify";
import { VFileUpload } from "vuetify/labs/VFileUpload";
import type { BreadcrumbItem } from "vuetify/lib/components/VBreadcrumbs/VBreadcrumbs.mjs";
import { ELessonTypes, type ILesson, type ILessonForm } from "~/types/lesson";

const route = useRoute();

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

const router = useRouter();

const formValues = ref<Partial<ILessonForm>>({
  isActive: true,
  type: ELessonTypes.VIDEO,
  orderIndex: 1,
});

const acceptFiles = computed(() => {
  switch (formValues.value?.type) {
    case ELessonTypes.VIDEO:
      return "video/*";
    case ELessonTypes.DOCUMENT:
      return ".pdf";
  }
});

const onCancel = () => {
  router.push(`/courses/${route.params.id}`);
};

const form = ref();
const onSave = async () => {
  const { valid } = await form.value.validate();

  if (!valid) return;
  toast("Create successfully", {
    type: "success",
  });
};

const lessonTypeOptions = [
  {
    title: "Video",
    value: "video",
    color: "blue",
  },
  {
    title: "PDF",
    value: "pdf",
    color: "purple",
  },
  {
    title: "Quiz",
    value: "quiz",
    color: "green",
  },
];

const lessonsData = ref<ILesson[]>(
  Array.from({ length: 10 }, (_, i) => {
    const type = lessonTypeOptions[i % lessonTypeOptions.length]!.value;

    const titles = {
      video: `Lesson ${i + 1}: Introduction to JavaScript`,
      pdf: `Lesson ${i + 1}: Advanced Concepts`,
      quiz: `Lesson ${i + 1}: Practice Quiz`,
    };

    return {
      id: (i + 1).toString(),
      orderIndex: i + 1,
      title: titles[type as keyof typeof titles],
      type,
      url: `https://example.com/lesson-${i + 1}`,
      isActive: i % 2 === 0,
    };
  })
);

const orderIndexDetail = computed(() => {
  const currentIndex = formValues.value.orderIndex ?? 1;
  return {
    before: lessonsData.value[currentIndex - 2]?.title,
    after: lessonsData.value[currentIndex - 1]?.title,
  };
});
</script>

<style lang="scss" scoped>
.create-lesson :deep {
  .v-slider-track__fill {
    display: none !important;
  }

  .v-slider-track__tick {
    background-color: black !important;
  }

  .v-slider-thumb__label {
    height: unset !important;
  }
}
</style>
