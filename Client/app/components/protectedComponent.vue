<template>
  <template v-if="isValid">
    <slot />
  </template>

  <template v-else>
    <slot name="fallback"></slot>
  </template>
</template>

<script setup lang="ts">
const auth = useAuth();
const props = defineProps<{ roles?: E_ROLES[] }>();

const isValid = computed(
  () => auth.role && (props.roles ? props.roles.includes(auth.role) : true)
);
</script>

<style scoped></style>
