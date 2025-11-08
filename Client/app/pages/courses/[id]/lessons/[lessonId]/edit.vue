<template>
  <div class="flex flex-col items-center px-3">
    <div class="w-full">
      <v-breadcrumbs :items="breadcrumbs" class="ps-0">
        <template v-slot:divider>
          <v-icon icon="mdi-chevron-right" />
        </template>
      </v-breadcrumbs>

      <v-card class="w-full">
        <v-card-text>
          <lesson-form-control v-model="formValues" ref="form" />
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
import { ELessonTypes, type ILessonForm } from "~/types/lesson";

const route = useRoute();

const breadcrumbs: BreadcrumbItem[] = [
  {
    title: "Courses",
    to: "/",
  },
  {
    title: "Ứng dụng phần mềm",
    to: `/courses/${route.params.id}`,
  },
  {
    title: "Create lesson",
  },
];

const router = useRouter();

const formValues = ref<Partial<ILessonForm>>({
  isActive: true,
  type: ELessonTypes.VIDEO,
  orderIndex: 1,
});

const onCancel = () => {
  router.push(`/courses/${route.params.id}`);
};

const form = ref();
const onSave = async () => {
  const { valid } = await form.value.validate();

  if (!valid) return;
  toast("Create successfully", {
    type: "success",
  });
};
</script>
