<template>
  <v-breadcrumbs :items="breadcrumbs" class="ps-0">
    <template v-slot:divider>
      <v-icon icon="mdi-chevron-right" />
    </template>
  </v-breadcrumbs>

  <protected-component :roles="[E_ROLES.STUDENT]">
    <courses-detail-for-student />
  </protected-component>

  <protected-component :roles="[E_ROLES.INSTRUCTOR]">
    <courses-detail-for-teacher />
  </protected-component>
</template>

<script setup lang="ts">
import type { BreadcrumbItem } from "vuetify/lib/components/VBreadcrumbs/VBreadcrumbs.mjs";

const breadcrumbs: BreadcrumbItem[] = [
  {
    title: "Courses",
    to: "/",
  },
  {
    title: "Ứng dụng phần mềm",
  },
];
</script>

<style scoped></style>
