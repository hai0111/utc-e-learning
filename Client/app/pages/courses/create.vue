<template>
  <div class="flex flex-col items-center px-4">
    <div class="w-full">
      <v-breadcrumbs :items="breadcrumbs" class="ps-0">
        <template v-slot:divider>
          <v-icon icon="mdi-chevron-right" />
        </template>
      </v-breadcrumbs>

      <v-card class="w-full">
        <v-card-text>
          <v-form class="grid grid-cols-2 gap-y-3 gap-x-9 mt-5" ref="form">
            <info-item label="Title">
              <v-text-field v-model="formValues.title" :rules="[required()]" />
            </info-item>

            <info-item label="Active">
              <v-switch inset v-model="formValues.isActive" color="primary" />
            </info-item>

            <info-item label="Description" class="col-span-2">
              <v-textarea
                v-model="formValues.description"
                :max-rows="9"
                auto-grow
                :rules="[required()]"
              />
            </info-item>
          </v-form>
        </v-card-text>
      </v-card>

      <div class="flex justify-end mt-3">
        <group-btn-form
          @click:cancel="onCancel"
          variant="elevated"
          @click:save="onSave"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toast } from "vue3-toastify";
import type { BreadcrumbItem } from "vuetify/lib/components/VBreadcrumbs/VBreadcrumbs.mjs";
import CourseService from "~/services/course.service";
import type { ICourseForm } from "~/types/course";

const breadcrumbs: BreadcrumbItem[] = [
  {
    title: "Courses",
    to: "/",
  },
  {
    title: "Create",
  },
];

const router = useRouter();

const formValues = ref<Partial<ICourseForm>>({
  isActive: true,
});

const onCancel = () => {
  router.push("/");
};

const form = ref();
const onSave = async () => {
  const { valid } = await form.value.validate();

  if (!valid) return;

  try {
    const res = await CourseService.create({
      ...formValues.value,
      userId: useAuth().userInfo?.uuid,
    } as ICourseForm);

    await navigateTo("/");

    toast("Create successfully", {
      type: "success",
    });
  } catch (err) {
    console.error(err);
  }
};
</script>

<style lang="scss"></style>
