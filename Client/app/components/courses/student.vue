<template>
  <v-breadcrumbs :items="breadcrumbs" class="ps-0">
    <template v-slot:divider>
      <v-icon icon="mdi-chevron-right" />
    </template>
  </v-breadcrumbs>
  <VDataTable :headers="headers" :items="data" disable-sort>
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

    <template #item.progressPercentage="{ item: { progressPercentage } }">
      <div class="flex items-center gap-4 whitespace-nowrap">
        {{ progressPercentage }}%
        <v-progress-linear
          color="blue"
          height="12"
          max="100"
          min="0"
          :model-value="progressPercentage"
          rounded
        />
      </div>
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
    key: "instructor",
    title: "Instructor",
  },

  {
    key: "progressPercentage",
    title: "Progress",
  },
];

const { data } = useAsyncData("courses", () => CourseService.getList(), {
  transform: (data) =>
    data.map((item) => ({
      ...item,
      progressPercentage: item.progressPercentage?.toFixed() ?? 30,
    })),
});
</script>

<style scoped></style>
