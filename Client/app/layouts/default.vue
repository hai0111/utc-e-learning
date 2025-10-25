<template>
  <main class="bg-[#f1f5f9] flex flex-col min-h-screen">
    <layout-main-header class="sticky top-0 z-10" />

    <div class="grow p-10">
      <slot />
    </div>
    <layout-main-footer />
  </main>
</template>

<script setup lang="ts"></script>

<style scoped></style>
