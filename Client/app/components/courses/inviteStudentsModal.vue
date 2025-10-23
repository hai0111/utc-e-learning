<template>
  <v-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    max-width="1200"
  >
    <v-card title="Add Students">
      <v-card-text>
        <v-text-field
          density="compact"
          variant="outlined"
          placeholder="Search by name or ID"
          v-model="searchStudentValue"
        />
        <v-data-table
          v-model="selectedStudents"
          :headers="lessonHeaders"
          show-select
          :items="students"
          item-value="studentId"
          color="primary"
        ></v-data-table>
      </v-card-text>

      <v-card-actions class="justify-end">
        <group-btn-form @click:cancel="$emit('update:modelValue', false)" />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { cloneDeep } from "lodash";
import type { DataTableHeader } from "vuetify";

defineProps<{
  modelValue: boolean;
}>();

defineEmits<{
  (e: "update:modelValue", value: boolean): void;
}>();

const studentsData = Array.from({ length: 50 }, (_, i) => {
  i += 50;
  return {
    studentId: `STU${(i + 1).toString().padStart(4, "0")}`,
    fullName: `Student ${i + 1}`,
  };
});

const searchStudentValue = ref<string>("");

const students = computed(() =>
  studentsData.filter(
    (item) =>
      toSlug(item.fullName).includes(toSlug(searchStudentValue.value)) ||
      toSlug(item.studentId).includes(toSlug(searchStudentValue.value))
  )
);

const selectedStudents = ref([]);

const lessonHeaders: DataTableHeader[] = [
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
</script>

<style scoped></style>
