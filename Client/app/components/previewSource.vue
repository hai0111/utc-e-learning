<template>
  <video
    v-if="type === ELessonTypes.VIDEO"
    autoplay
    controls
    :src="src"
  ></video>
  <iframe v-else-if="type === ELessonTypes.DOCUMENT" :src="src"></iframe>
</template>

<script setup lang="ts">
import { ELessonTypes } from "~/types/lesson";

defineProps<{
  type: ELessonTypes;
  src: string;
}>();
</script>

<style scoped></style>
