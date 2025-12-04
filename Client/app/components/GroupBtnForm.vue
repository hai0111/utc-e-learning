<template>
  <div class="flex items-center gap-2">
    <v-btn
      :density="density"
      :variant="variant"
      color="red"
      append-icon="mdi-close"
      @click="$emit('click:cancel')"
    >
      {{ cancelText }}
    </v-btn>
    <v-btn
      :density="density"
      :variant="variant"
      color="success"
      append-icon="mdi-check"
      @click="$emit('click:save')"
      :loading="loading"
    >
      {{ saveText }}
    </v-btn>
  </div>
</template>

<script setup lang="ts">
import type { VBtn } from "vuetify/components";
import type { Density } from "vuetify/lib/composables/density.mjs";

withDefaults(
  defineProps<{
    density?: Density;
    variant?: VBtn["variant"];
    saveText?: string;
    cancelText?: string;
    loading?: boolean;
  }>(),
  {
    density: "default",
    cancelText: "Cancel",
    saveText: "Save",
    variant: "text",
  }
);

defineEmits<{
  (e: "click:cancel"): void;
  (e: "click:save"): void;
}>();
</script>

<style scoped></style>
