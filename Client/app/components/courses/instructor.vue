<template>
  <v-breadcrumbs :items="breadcrumbs" class="ps-0">
    <template v-slot:divider>
      <v-icon icon="mdi-chevron-right" />
    </template>
  </v-breadcrumbs>

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
  </VDataTable>
</template>

<script setup lang="ts">
import type { DataTableHeader } from "vuetify";
import type { BreadcrumbItem } from "vuetify/lib/components/VBreadcrumbs/VBreadcrumbs.mjs";

const breadcrumbs: BreadcrumbItem[] = [
  {
    title: "Courses",
  },
];

const headers: DataTableHeader[] = [
  {
    key: "index",
    title: "Index",
  },

  {
    key: "title",
    title: "Title",
  },

  {
    key: "numberOfStudents",
    title: "Number of students",
  },
];

const items = ref([
  {
    id: 1,
    title: "Ứng dụng phần mềm",
    numberOfStudents: 30,
  },
]);
</script>

<style scoped></style>
