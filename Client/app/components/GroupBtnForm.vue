<template>
  <div class="flex items-center gap-2">
    <v-btn
      :density="density"
      variant="text"
      color="red"
      append-icon="mdi-close"
      @click="$emit('click:cancel')"
    >
      {{ cancelText }}
    </v-btn>
    <v-btn
      :density="density"
      variant="text"
      color="success"
      append-icon="mdi-check"
      @click="$emit('click:save')"
    >
      {{ saveText }}
    </v-btn>
  </div>
</template>

<script setup lang="ts">
import type { Density } from "vuetify/lib/composables/density.mjs";

withDefaults(
  defineProps<{
    density?: Density;
    saveText?: string;
    cancelText?: string;
  }>(),
  {
    density: "default",
    cancelText: "Cancel",
    saveText: "Save",
  }
);

defineEmits<{
  (e: "click:cancel"): void;
  (e: "click:save"): void;
}>();
</script>

<style scoped></style>
