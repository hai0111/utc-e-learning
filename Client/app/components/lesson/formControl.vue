<template>
  <v-form
    class="grid grid-cols-2 gap-y-3 gap-x-9 mt-5 create-lesson"
    ref="form"
  >
    <info-item label="Title">
      <v-text-field v-model="modelValue.title" :rules="[required()]" />
    </info-item>

    <info-item label="Active">
      <v-switch inset v-model="modelValue.isActive" color="primary" />
    </info-item>

    <div class="flex items-center gap-3">
      Choose type:
      <v-btn-toggle v-model="modelValue.type" color="primary" density="compact">
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

    <div class="col-span-2 grid grid-cols-[auto_1fr] items-center gap-3 mt-6">
      <div>Order Index:</div>

      <v-slider
        v-model="modelValue.orderIndex"
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
          <template v-if="(modelValue.orderIndex ?? 1) > 1">
            Before:
            <div>
              <strong> {{ orderIndexDetail.before }} </strong>
            </div>
          </template>
        </div>

        <div>
          <template v-if="(modelValue.orderIndex ?? 0) < lessonsData.length">
            After:
            <div>
              <strong> {{ orderIndexDetail.after }} </strong>
            </div>
          </template>
        </div>
      </div>
    </div>
  </v-form>
</template>

<script setup lang="ts">
import { cloneDeep } from "lodash";
import { VFileUpload } from "vuetify/labs/VFileUpload";
import { ELessonTypes, type ILesson, type ILessonForm } from "~/types/lesson";

const props = withDefaults(
  defineProps<{ modelValue: Partial<ILessonForm> }>(),
  {
    modelValue() {
      return cloneDeep({
        isActive: true,
        type: ELessonTypes.VIDEO,
        orderIndex: 1,
      });
    },
  }
);

const emit = defineEmits<{
  (e: "update:modelValue", modelValue: Partial<ILessonForm>): void;
}>();

const form = ref();

defineExpose({ validate: () => form.value?.validate() });

const acceptFiles = computed(() => {
  switch (props.modelValue.type) {
    case ELessonTypes.VIDEO:
      return "video/*";
    case ELessonTypes.DOCUMENT:
      return ".pdf";
  }
});

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
      url: `https://example.com/lesson-${i + 1}`,
      isActive: i % 2 === 0,
    };
  })
);

const orderIndexDetail = computed(() => {
  const currentIndex = props.modelValue.orderIndex ?? 1;
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
