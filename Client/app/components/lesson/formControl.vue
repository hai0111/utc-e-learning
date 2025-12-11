<template>
  <v-form
    :disabled="disabled"
    class="grid grid-cols-2 gap-y-3 gap-x-9 mt-5 create-lesson"
    ref="form"
  >
    <info-item label="Title">
      <v-text-field v-model="modelValue.title" :rules="[required()]" />
    </info-item>

    <info-item label="Active">
      <v-switch inset v-model="modelValue.isActive" color="primary" />
    </info-item>

    <template v-if="mode === EFormType.CREATE">
      <div class="flex items-center gap-3">
        Choose type:
        <v-btn-toggle
          v-model="modelValue.type"
          :disabled="disabled"
          color="primary"
          density="compact"
        >
          <v-btn :value="ELessonTypes.VIDEO">
            <v-icon>mdi-video</v-icon>
          </v-btn>

          <v-btn :value="ELessonTypes.DOCUMENT">
            <v-icon>mdi-file-pdf-box </v-icon>
          </v-btn>

          <v-btn :value="ELessonTypes.QUIZ">
            <v-icon>mdi-head-question </v-icon>
          </v-btn>
        </v-btn-toggle>
      </div>

      <div class="col-span-2">
        <VFileUpload
          :disabled="disabled"
          v-model="modelValue.file"
          density="comfortable"
          variant="comfortable"
          :filter-by-type="acceptFiles"
        />
      </div>
    </template>

    <div v-else-if="modelValue.url" class="col-span-2 w-[1000px]">
      <preview-source
        :type="modelValue.type!"
        :src="modelValue.url"
        class="w-[1000px] h-[60vh]"
      />
    </div>

    <div class="col-span-2 grid grid-cols-[auto_1fr] items-center gap-3 mt-6">
      <div>Order Index:</div>

      <v-slider
        v-model="modelValue.orderIndex"
        show-ticks="always"
        step="1"
        :min="1"
        :max="data.length + 1"
        tick-size="4"
        thumb-size="15"
        thumb-label="always"
        hide-details
      />

      <div></div>

      <div class="flex gap-2 justify-between">
        <div>
          <template v-if="orderIndexDetail.before">
            Before:
            <div>
              <strong> {{ orderIndexDetail.before }} </strong>
            </div>
          </template>
        </div>

        <div>
          <template v-if="orderIndexDetail.after">
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
import { EFormType } from "~/constants/common";
import CourseService from "~/services/course.service";
import { ELessonTypes, type ILessonForm } from "~/types/lesson";

const props = withDefaults(
  defineProps<{
    modelValue: Partial<ILessonForm>;
    mode?: EFormType;
    disabled?: boolean;
  }>(),
  {
    modelValue() {
      return cloneDeep({
        isActive: true,
        type: ELessonTypes.VIDEO,
        orderIndex: 1,
      });
    },
    mode: EFormType.CREATE,
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

const { params } = useRoute();

const { data } = useAsyncData(
  `course/${params.id as string}/lessons-for-create-and-edit`,
  () => CourseService.getLessons((params.id as string) ?? ""),
  {
    default: () => [],
    transform(data) {
      return data.filter((item) => {
        return item.id !== params.lessonId;
      });
    },
  }
);

const orderIndexDetail = computed(() => {
  let currentIndex = (props.modelValue.orderIndex ?? 1) - 1;

  return {
    before: data.value[currentIndex - 1]?.title,
    after: data.value[currentIndex]?.title,
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
