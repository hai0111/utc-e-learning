<template>
  <ProtectedComponent :roles="[E_ROLES.STUDENT]">
    <CoursesStudent />
  </ProtectedComponent>

  <ProtectedComponent :roles="[E_ROLES.INSTRUCTOR]">
    <CoursesInstructor />
  </ProtectedComponent>
</template>

<script setup lang="ts"></script>

<style scoped></style>
