<template>
  <template v-if="!reRendering">
    <vue-plyr v-if="type === ELessonTypes.VIDEO">
      <video
        :src="src"
        v-bind="$attrs"
        ref="videoRef"
        @timeupdate="$emit('timeUpdateVideo', $event)"
      ></video>
    </vue-plyr>

    <iframe
      v-else-if="type === ELessonTypes.DOCUMENT"
      :src="src"
      v-bind="$attrs"
    />
  </template>
</template>

<script setup lang="ts">
import { ELessonTypes } from "~/types/lesson";

const props = defineProps<{
  type: ELessonTypes;
  src: string;
  currentPercent?: number;
}>();

const emit = defineEmits<{ (e: "timeUpdateVideo", event: Event): void }>();

const reRendering = ref(false);
const videoRef = ref<HTMLVideoElement>();

watch(
  () => props.src,
  () => {
    reRendering.value = true;
    setTimeout(() => {
      reRendering.value = false;
      nextTick(() => {
        if (videoRef.value) {
          videoRef.value.addEventListener("loadedmetadata", () => {
            if (videoRef.value && props.currentPercent)
              videoRef.value.currentTime =
                (props.currentPercent / 100) * videoRef.value.duration;
          });
        }
      });
    }, 200);
  },
  {
    immediate: true,
  }
);
</script>

<style scoped></style>
