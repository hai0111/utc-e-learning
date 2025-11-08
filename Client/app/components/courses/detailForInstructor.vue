<template>
  <div class="grid grid-cols-[2fr_1fr] items-start gap-3">
    <div>
      <v-card>
        <v-card-title class="flex justify-end">
          <v-btn
            v-if="!isEditBasicInfo"
            variant="text"
            color="primary"
            append-icon="mdi-pencil"
            @click="isEditBasicInfo = true"
          >
            Edit
          </v-btn>
        </v-card-title>
        <v-card-text>
          <div class="grid grid-cols-2 gap-y-3 gap-x-9 mt-5">
            <info-item label="Title">
              <template v-if="isEditBasicInfo">
                <v-text-field v-model="basicInfo.title" />
              </template>

              <template v-else>
                {{ basicInfo.title }}
                <div class="col-span-2 font-weight-thin mt-1 text-sm">
                  Last updated: 01/11/2025
                </div>
              </template>
            </info-item>

            <template v-if="isEditBasicInfo">
              <info-item label="Active">
                <v-switch inset v-model="basicInfo.isActive" color="primary" />
              </info-item>
            </template>

            <template v-else>
              <div>
                <custom-chip
                  :items="ACTIVE_ITEMS"
                  :model-value="basicInfo.isActive"
                />
              </div>
            </template>

            <info-item label="Description" class="col-span-2">
              <template v-if="isEditBasicInfo">
                <v-textarea
                  v-model="basicInfo.description"
                  :max-rows="9"
                  auto-grow
                />
              </template>

              <template v-else> {{ basicInfo.description }} </template>
            </info-item>
          </div>
        </v-card-text>

        <v-card-actions class="justify-end">
          <group-btn-form v-if="isEditBasicInfo" @click:cancel="onCancelEdit" />
        </v-card-actions>
      </v-card>

      <courses-lessons-for-instructor />
    </div>

    <courses-students-for-instructor />
  </div>
</template>

<script setup lang="ts">
import { cloneDeep } from "lodash";
import { ACTIVE_ITEMS } from "~/constants";
import CourseService from "~/services/course.service";
import type { ICourse } from "~/types/course";

const { params } = useRoute();

const { data } = useAsyncData(
  `course/${params.id as string}`,
  () => CourseService.detail((params.id as string) ?? ""),
  { default: () => ({}) }
);

const isEditBasicInfo = ref(false);

const basicInfo = ref<Partial<ICourse>>({});

const onCancelEdit = () => {
  basicInfo.value = cloneDeep(data.value);
  isEditBasicInfo.value = false;
};

watch(data, (val) => {
  basicInfo.value = cloneDeep(val);
});
</script>

<style scoped></style>
