<template>
  <v-card class="mt-3">
    <v-card-title class="flex justify-space-between">
      Lesson List

      <v-btn variant="text" color="success" append-icon="mdi-plus"> Add </v-btn>
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
        :sort-by="[{ key: 'order', order: 'asc' }]"
        class="max-h-[500px]"
        item-value="order"
      >
        <template #item.order>
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

        <template #item.url>
          <v-btn variant="text" icon="mdi-eye" color="blue" />
        </template>

        <template #item.actions>
          <v-btn variant="text" icon color="blue" size="30">
            <v-icon icon="mdi-pencil" size="18" />
          </v-btn>

          <v-btn variant="text" icon color="red" size="30">
            <v-icon icon="mdi-delete" size="18" />
          </v-btn>
        </template>
      </v-data-table>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { cloneDeep, isEqual } from "lodash";
import Sortable from "sortablejs";
import type { VDataTable } from "vuetify/components";
const lessonHeaders = [
  {
    key: "order",
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
    value: "video",
    color: "blue",
  },
  {
    title: "PDF",
    value: "pdf",
    color: "purple",
  },
  {
    title: "Quiz",
    value: "quiz",
    color: "green",
  },
];

const lessonsDataOriginal = ref(
  Array.from({ length: 10 }, (_, i) => {
    const type = lessonTypeOptions[i % lessonTypeOptions.length]?.value;

    const titles = {
      video: `Lesson ${i + 1}: Introduction to JavaScript`,
      pdf: `Lesson ${i + 1}: Advanced Concepts`,
      quiz: `Lesson ${i + 1}: Practice Quiz`,
    };

    return {
      order: i + 1,
      title: titles[type as keyof typeof titles],
      type,
      url: `https://example.com/lesson-${i + 1}`,
      isActive: i % 2 === 0,
      actions: "",
    };
  })
);

const lessonsData = ref(cloneDeep(lessonsDataOriginal.value));

const isDifferent = computed(() => {
  return !isEqual(lessonsData.value, lessonsDataOriginal.value);
});

const searchLessonValue = ref<string>("");

const lessons = computed(() => {
  return lessonsData.value.filter((l) =>
    toSlug(l.title).includes(toSlug(searchLessonValue.value))
  );
});

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
</script>

<style scoped></style>
