<template>
  <v-card class="mt-3">
    <v-card-title class="flex justify-space-between">
      Lesson List

      <v-btn
        variant="text"
        color="success"
        append-icon="mdi-plus"
        @click="$router.push(`/courses/${$route.params.id}/lessons/create`)"
      >
        Add
      </v-btn>
    </v-card-title>

    <v-card-subtitle> Total: 21 </v-card-subtitle>

    <v-card-text class="pt-5">
      <v-text-field
        density="compact"
        variant="outlined"
        placeholder="Search by title"
        v-model="searchLessonValue"
      />

      <div v-if="isDifferent" class="flex items-center justify-end mb-1">
        <group-btn-form @click:cancel="cancelEditLessons" />
      </div>

      <v-data-table
        ref="lessonTable"
        disable-sort
        :headers="lessonHeaders"
        :items="lessons"
        items-per-page="0"
        hide-default-footer
        sticky
        :sort-by="[{ key: 'orderIndex', order: 'asc' }]"
        class="max-h-[500px]"
        item-value="orderIndex"
      >
        <template #item.orderIndex>
          <v-icon
            icon="mdi-drag-horizontal-variant"
            class="cursor-pointer sort-btn"
          />
        </template>

        <template #item.type="{ value }">
          <custom-chip :items="lessonTypeOptions" :model-value="value" />
        </template>

        <template #item.isActive="{ item }">
          <v-switch hide-details color="primary" v-model="item.isActive" />
        </template>

        <template #item.url="{ item }">
          <v-btn
            variant="text"
            icon="mdi-eye"
            color="blue"
            @click="openPreviewModal(item)"
          />
        </template>

        <template #item.actions="{ item }">
          <v-btn
            variant="text"
            icon
            color="blue"
            size="30"
            @click="
              $router.push(
                `/courses/${$route.params.id}/lessons/${item.id}/edit`
              )
            "
          >
            <v-icon icon="mdi-pencil" size="18" />
          </v-btn>

          <v-btn
            variant="text"
            icon
            color="red"
            size="30"
            @click="openDeleteModal(item)"
          >
            <v-icon icon="mdi-delete" size="18" />
          </v-btn>
        </template>
      </v-data-table>
    </v-card-text>
  </v-card>

  <v-dialog v-model="deleteModal.isOpen" max-width="500">
    <v-card>
      <v-card-text>
        <v-icon icon="mdi-alert" color="warning" />
        Are you sure you want to delete the course
        <br />
        <strong>“{{ deleteModal.lesson?.title }}”</strong>?
      </v-card-text>
      <v-card-actions>
        <group-btn-form @click:cancel="deleteModal.isOpen = false" />
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-dialog v-model="previewModal.isOpen" width="max-content">
    <v-card>
      <v-card-text>
        <preview-source
          v-if="previewModal.lesson"
          :type="previewModal.lesson.type"
          src="/Chuong3_TKCoDoiThu.pdf"
          class="h-[80vh] min-w-[1200px] max-w-[80vw]"
        />
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { cloneDeep, isEqual } from "lodash";
import Sortable from "sortablejs";
import type { VDataTable } from "vuetify/components";
import { ELessonTypes, type ILesson } from "~/types/lesson";

const lessonHeaders = [
  {
    key: "orderIndex",
  },

  {
    key: "title",
    title: "Title",
  },

  {
    key: "type",
    title: "Type",
  },

  {
    key: "url",
    title: "Source",
  },

  {
    key: "isActive",
    title: "Active",
  },
  {
    key: "actions",
    width: 130,
    sortable: false,
  },
];

const lessonTypeOptions = [
  {
    title: "Video",
    value: ELessonTypes.VIDEO,
    color: "blue",
  },
  {
    title: "PDF",
    value: ELessonTypes.DOCUMENT,
    color: "purple",
  },
  {
    title: "Quiz",
    value: ELessonTypes.QUIZ,
    color: "green",
  },
];

const lessonsDataOriginal = ref<ILesson[]>(
  Array.from({ length: 10 }, (_, i) => {
    const type = lessonTypeOptions[i % lessonTypeOptions.length]!.value;

    const titles = {
      [ELessonTypes.VIDEO]: `Lesson ${i + 1}: Introduction to JavaScript`,
      [ELessonTypes.DOCUMENT]: `Lesson ${i + 1}: Advanced Concepts`,
      [ELessonTypes.QUIZ]: `Lesson ${i + 1}: Practice Quiz`,
    };

    return {
      id: (i + 1).toString(),
      orderIndex: i + 1,
      title: titles[type as keyof typeof titles],
      type,
      url: `https://example.com/lesson-${i + 1}`,
      isActive: i % 2 === 0,
    };
  })
);

const lessonsData = ref(cloneDeep(lessonsDataOriginal.value));

const searchLessonValue = ref<string>("");

const lessons = computed(() => {
  return lessonsData.value.filter((l) =>
    toSlug(l.title).includes(toSlug(searchLessonValue.value))
  );
});

const isDifferent = computed(() => {
  return !isEqual(lessonsData.value, lessonsDataOriginal.value);
});

// SORT ============================================
const lessonTable = ref<InstanceType<typeof VDataTable>>();

const cancelEditLessons = () => {
  lessonsData.value = cloneDeep(lessonsDataOriginal.value);
};

onMounted(() => {
  const tbody = (lessonTable.value?.$el as HTMLTableElement).querySelector(
    "tbody"
  );
  if (!tbody) return;
  Sortable.create(tbody, {
    handle: ".sort-btn",
    onEnd({ oldIndex, newIndex }) {
      const clonedData = cloneDeep(lessonsData.value);
      if (oldIndex === undefined || newIndex === undefined) return;
      const [movedItem] = clonedData.splice(oldIndex, 1);
      clonedData.splice(newIndex, 0, movedItem!);
      lessonsData.value = clonedData;
    },
  });
  return;
});

// DELETE ============================================
const deleteModal = ref<{ isOpen: boolean; lesson: null | ILesson }>({
  isOpen: false,
  lesson: null,
});

const openDeleteModal = (data: ILesson) => {
  deleteModal.value.lesson = data;
  deleteModal.value.isOpen = true;
};

// PREVIEW ============================================

const previewModal = ref<{ isOpen: boolean; lesson: null | ILesson }>({
  isOpen: false,
  lesson: null,
});

const openPreviewModal = (data: ILesson) => {
  previewModal.value.lesson = data;
  previewModal.value.isOpen = true;
};
</script>

<style scoped></style>
