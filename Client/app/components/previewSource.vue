<template>
  <template v-if="!reRendering">
    <vue-plyr v-if="type === ELessonTypes.VIDEO">
      <video :src="src" v-bind="$attrs"></video>
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
}>();

const reRendering = ref(false);

watch(
  () => props.src,
  () => {
    reRendering.value = true;
    nextTick(() => {
      setTimeout(() => {
        reRendering.value = false;
      }, 200);
    });
  }
);
</script>

<style scoped></style>
