<template>
  <v-chip :color="lookup.get(modelValue)?.color" density="compact">
    {{ lookup.get(modelValue)?.title }}
  </v-chip>
</template>

<script setup lang="ts">
import type { IColorItem } from "~/shared/types";

const props = defineProps<{
  items: IColorItem[];
  modelValue?: IColorItem["value"];
}>();

const lookup = computed(() => toLookup(props.items, "value"));
</script>

<style scoped></style>
