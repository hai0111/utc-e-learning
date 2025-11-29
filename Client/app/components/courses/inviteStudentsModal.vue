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
          placeholder="Search by name or code"
          v-model="searchStudentValue"
        />
        <v-data-table
          v-model="selectedStudents"
          :headers="lessonHeaders"
          show-select
          :items="students"
          item-value="id"
          color="primary"
          class="max-h-[500px]"
        ></v-data-table>
      </v-card-text>

      <v-card-actions class="justify-end">
        <group-btn-form
          @click:cancel="$emit('update:modelValue', false)"
          @click:save="onSave"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { toast } from "vue3-toastify";
import type { DataTableHeader } from "vuetify";
import CourseService from "~/services/course.service";

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
  (e: "success"): void;
}>();

const { params } = useRoute();

const { data, refresh } = useAsyncData(
  `course/${params.id as string}/student-not-course`,
  () => CourseService.getStudentsNotEnrolled((params.id as string) ?? ""),
  { default: () => [] }
);

defineExpose({ refresh });

const searchStudentValue = ref<string>("");

const students = computed(() =>
  data.value.filter(
    (item) =>
      toSlug(item.name).includes(toSlug(searchStudentValue.value)) ||
      toSlug(item.code).includes(toSlug(searchStudentValue.value))
  )
);

const selectedStudents = ref([]);

const lessonHeaders: DataTableHeader[] = [
  {
    key: "code",
    title: "Student Code",
  },

  {
    key: "name",
    title: "Fullname",
  },

  {
    key: "actions",
    sortable: false,
  },
];

const onSave = async () => {
  const studentIds = Array.from(selectedStudents.value);
  try {
    await CourseService.addStudentToCourse(params.id as string, studentIds);
    toast("Successfully", { type: "success" });
    emit("update:modelValue", false);
    refresh();
    selectedStudents.value = [];
    emit("success");
  } catch (err) {
    console.error(err);
    toast("Something went wrong", { type: "error" });
  }
};
</script>

<style scoped></style>
