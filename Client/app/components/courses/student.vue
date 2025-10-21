<template>
  <VDataTable :headers="headers" :items="items" disable-sort>
    <template #item.index="{ index }"> {{ index + 1 }} </template>
    <template #item.title="{ item: { id, title } }">
      <NuxtLink
        :to="`/courses/${id}`"
        :title="title"
        class="text-blue-500 font-semibold"
      >
        {{ title }}
      </NuxtLink>
    </template>

    <template #item.progress="{ item: { progress } }">
      <div class="flex items-center gap-4 whitespace-nowrap">
        {{ progress }}%
        <v-progress-linear
          color="blue"
          height="12"
          max="100"
          min="0"
          :model-value="progress"
          rounded
        />
      </div>
    </template>
  </VDataTable>
</template>

<script setup lang="ts">
import type { DataTableHeader } from "vuetify";

const headers: DataTableHeader[] = [
  {
    key: "index",
    title: "STT",
  },

  {
    key: "title",
    title: "Title",
  },

  {
    key: "instructorName",
    title: "Instructor",
  },

  {
    key: "progress",
    title: "Progress",
  },
];

const items = ref([
  {
    id: 1,
    title: "Ứng dụng phần mềm",
    instructorName: "Nguyễn Đức Dư",
    progress: 30,
  },
]);
</script>

<style scoped></style>
