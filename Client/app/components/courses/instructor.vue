<template>
  <div class="flex justify-between items-end">
    <v-breadcrumbs :items="breadcrumbs" class="ps-0">
      <template v-slot:divider>
        <v-icon icon="mdi-chevron-right" />
      </template>
    </v-breadcrumbs>

    <v-btn
      color="success"
      append-icon="mdi-plus"
      class="mb-3"
      to="/courses/create"
    >
      Create new
    </v-btn>
  </div>

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
import CourseService from "~/services/course.service";

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

const { data } = useAsyncData("courses", () => CourseService.getList());
</script>

<style scoped></style>
