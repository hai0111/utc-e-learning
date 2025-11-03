<template>
  <template v-if="auth.userInfo">
    <slot></slot>
  </template>
</template>

<script setup lang="ts">
const auth = useAuth();
onMounted(() => {
  if (!auth.userInfo) auth.getUserInfo();
});
</script>
