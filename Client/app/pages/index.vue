<template>
  <ProtectedComponent :roles="[E_ROLES.STUDENT]">
    <CoursesStudent />
  </ProtectedComponent>
</template>

<script setup lang="ts"></script>

<style scoped></style>
