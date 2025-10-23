<template>
  <div class="flex items-center justify-end mb-4">
    <v-btn
      :density="density"
      variant="text"
      color="red"
      append-icon="mdi-close"
      @click="$emit('click:cancel')"
    >
      Cancel
    </v-btn>
    <v-btn
      :density="density"
      variant="text"
      color="success"
      append-icon="mdi-check"
      @click="$emit('click:save')"
    >
      Save
    </v-btn>
  </div>
</template>

<script setup lang="ts">
import type { Density } from "vuetify/lib/composables/density.mjs";

withDefaults(
  defineProps<{
    density?: Density;
  }>(),
  {
    density: "default",
  }
);

defineEmits<{
  (e: "click:cancel"): void;
  (e: "click:save"): void;
}>();
</script>

<style scoped></style>
