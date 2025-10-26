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

      <courses-lessons-for-teacher />
    </div>

    <courses-students-for-teacher />
  </div>
</template>

<script setup lang="ts">
import { cloneDeep } from "lodash";
import { ACTIVE_ITEMS } from "~/constants";

const isEditBasicInfo = ref(false);

const basicInfoOrigin = ref({
  title: "Ứng dụng phần mềm",
  description:
    "Lorem ipsum dolor sit amet consectetur adipisicing elit. Praesentium non consequuntur voluptatum alias dolorem, dolor maiores debitis incidunt rem, rerum laborum ea ullam. Sint, perspiciatis? Suscipit ex placeat repudiandae provident nam error totam quis. Reprehenderit aspernatur voluptatibus aperiam corporis tenetur quo esse quidem magnam autem inventore pariatur dolores animi non temporibus perspiciatis corrupti quae sapiente similique, vitae eligendi tempore eveniet! Quis, officia incidunt, ullam libero, asperiores soluta sunt itaque enim vitae hic illo pariatur ducimus neque magnam necessitatibus dicta sequi magni tempora nulla impedit! Inventore error numquam fugit sunt accusamus nihil excepturi, aliquam vitae pariatur ab in voluptate tenetur maiores voluptas animi doloremque dolor delectus debitis odit ad nostrum architecto quaerat. Eligendi perspiciatis neque delectus veritatis voluptas magnam, tempora",
  isActive: true,
});

const basicInfo = ref(cloneDeep(basicInfoOrigin.value));

const onCancelEdit = () => {
  basicInfo.value = cloneDeep(basicInfoOrigin.value);
  isEditBasicInfo.value = false;
};
</script>

<style scoped></style>
