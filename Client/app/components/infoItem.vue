<template>
  <div
    :class="{
      'flex items-center gap-3': inline,
    }"
  >
    <div>
      {{ props.label }}
    </div>

    <div class="font-semibold mt-1">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  label: string;
  inline?: boolean;
}>();
</script>

<style scoped></style>
