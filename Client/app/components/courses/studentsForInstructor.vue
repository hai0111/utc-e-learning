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
        placeholder="Search by name or code"
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
          <v-btn
            variant="text"
            color="red"
            icon="mdi-delete"
            @click="openDeleteModal(item)"
          />
        </template>
      </v-data-table>
    </v-card-text>
  </v-card>

  <courses-invite-students-modal v-model="isOpenAdd" />

  <v-dialog v-model="deleteModal.isOpen" max-width="400">
    <v-card>
      <v-card-text>
        <v-icon icon="mdi-alert" color="warning" />
        Are you sure you want to unenroll
        <strong>“{{ deleteModal.student?.name }}”</strong> from this course?
      </v-card-text>
      <v-card-actions>
        <group-btn-form
          @click:cancel="deleteModal.isOpen = false"
          @click:save="handleDelete"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { toast } from "vue3-toastify";
import type { DataTableHeader } from "vuetify";
import type { VDataTable } from "vuetify/components";
import CourseService from "~/services/course.service";
import type { IStudent } from "~/types/student";

const headers: DataTableHeader[] = [
  {
    key: "index",
    title: "Index",
    sortable: false,
  },

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

const { params } = useRoute();

const { data } = useAsyncData(
  `course/${params.id as string}/students`,
  () => CourseService.getStudents((params.id as string) ?? ""),
  { default: () => [] }
);

const searchStudentValue = ref<string>("");

const students = computed(() =>
  data.value.filter(
    (item) =>
      toSlug(item.name).includes(toSlug(searchStudentValue.value)) ||
      toSlug(item.code).includes(toSlug(searchStudentValue.value))
  )
);

const isOpenAdd = ref(false);

const deleteModal = ref<{ isOpen: boolean; student: null | IStudent }>({
  isOpen: false,
  student: null,
});

const openDeleteModal = (data: IStudent) => {
  deleteModal.value.student = data;
  deleteModal.value.isOpen = true;
};

const handleDelete = async () => {
  if (!deleteModal.value.student) return;

  try {
    await CourseService.removeStudent(
      params.id as string,
      deleteModal.value.student.id
    );

    toast("Deleted successfully", { type: "success" });
  } catch (err) {
    toast("An error occurred", { type: "error" });
  }

  deleteModal.value.isOpen = false;
};
</script>

<style scoped></style>
