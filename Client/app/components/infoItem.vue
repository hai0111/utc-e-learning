<template>
  <div>
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
}>();
</script>

<style scoped></style>
