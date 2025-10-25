<template>
  <v-card>
    <v-card-title class="flex justify-space-between">
      Student List

      <v-btn
        variant="text"
        color="success"
        append-icon="mdi-plus"
        @click="isOpenAdd = true"
      >
        Invite
      </v-btn>
    </v-card-title>

    <v-card-subtitle>Total: {{ students.length }}</v-card-subtitle>
    <v-card-text class="pt-5">
      <v-text-field
        density="compact"
        variant="outlined"
        placeholder="Search by name or ID"
        v-model="searchStudentValue"
      />
      <v-data-table
        items-per-page="0"
        hide-default-footer
        :headers="headers"
        :items="students"
        class="max-h-[600px]"
      >
        <template #item.index="{ index }">
          {{ index + 1 }}
        </template>

        <template #item.actions="{ item }">
          <v-btn variant="text" color="red" icon="mdi-delete" />
        </template>
      </v-data-table>
    </v-card-text>
  </v-card>

  <courses-invite-students-modal v-model="isOpenAdd" />
</template>

<script setup lang="ts">
import type { DataTableHeader } from "vuetify";
import type { VDataTable } from "vuetify/components";

const headers: DataTableHeader[] = [
  {
    key: "index",
    title: "Index",
    sortable: false,
  },

  {
    key: "studentId",
    title: "Student ID",
  },

  {
    key: "fullName",
    title: "Fullname",
  },

  {
    key: "actions",
    sortable: false,
  },
];

const studentsData = Array.from({ length: 50 }, (_, i) => ({
  studentId: `STU${(i + 1).toString().padStart(4, "0")}`,
  fullName: `Student ${i + 1}`,
}));

const searchStudentValue = ref<string>("");

const students = computed(() =>
  studentsData.filter(
    (item) =>
      toSlug(item.fullName).includes(toSlug(searchStudentValue.value)) ||
      toSlug(item.studentId).includes(toSlug(searchStudentValue.value))
  )
);

const isOpenAdd = ref(false);
</script>

<style scoped></style>
