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

    <v-card-subtitle> Total: {{ lessonsData.length }}</v-card-subtitle>

    <v-card-text class="pt-5">
      <v-text-field
        density="compact"
        variant="outlined"
        placeholder="Search by title"
        v-model="searchLessonValue"
      />

      <div v-if="isDifferent" class="flex items-center justify-end mb-1">
        <group-btn-form
          @click:cancel="cancelEditLessons"
          @click:save="handleBatchUpdate"
        />
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
        <group-btn-form
          @click:cancel="deleteModal.isOpen = false"
          @click:save="handleDelete"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-dialog v-model="previewModal.isOpen" width="max-content">
    <v-card>
      <v-card-text>
        <preview-source
          v-if="previewModal.lesson"
          :type="previewModal.lesson.type"
          :src="previewModal.lesson.url"
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
import { lessonTypeOptions } from "~/constants/lesson";
import { DEFAULT_MESSAGES } from "~/constants/messages";
import CourseService from "~/services/course.service";
import {
  ELessonTypes,
  type ILesson,
  type ILessonBatchUpdateForm,
} from "~/types/lesson";

const { params } = useRoute();

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

const { data: lessonsDataOriginal, refresh } = useAsyncData(
  `course/${params.id as string}/lessons`,
  () => CourseService.getLessons((params.id as string) ?? ""),
  { default: () => [] }
);

const lessonsData = ref<ILesson[]>([]);

watch(
  lessonsDataOriginal,
  (val) => {
    lessonsData.value = cloneDeep(val);
  },
  { immediate: true }
);

const searchLessonValue = ref<string>("");

const lessons = computed(() => {
  return lessonsData.value.filter((l) =>
    toSlug(l.title).includes(toSlug(searchLessonValue.value))
  );
});

// BATCH UPDATE ============================================
const isDifferent = computed(() => {
  return !isEqual(lessonsData.value, lessonsDataOriginal.value);
});

const handleBatchUpdate = async () => {
  const body: ILessonBatchUpdateForm = {
    lessons: lessonsData.value.map((item) => ({
      id: item.id,
      isActive: item.isActive,
      orderIndex: item.orderIndex,
    })),
  };

  try {
    await CourseService.batchUpdateLessons(params.id as string, body);
    toastSuccess(DEFAULT_MESSAGES.apiSuccess);
    refresh();
  } catch (err) {
    console.error(err);
    toastError(DEFAULT_MESSAGES.apiError);
  }
};

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
      clonedData.splice(newIndex, 0, movedItem as ILesson);
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

const handleDelete = async () => {
  try {
    await CourseService.removeLesson(
      params.id as string,
      deleteModal.value.lesson!.id
    );

    toastSuccess(DEFAULT_MESSAGES.apiSuccess);
    refresh();
  } catch (err) {
    console.error(err);
    toastError(DEFAULT_MESSAGES.apiError);
  }
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
