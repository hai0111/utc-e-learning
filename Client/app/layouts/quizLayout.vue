<template>
  <AuthenticationWrapper>
    <main class="bg-[#f1f5f9] flex flex-col min-h-screen">
      <div class="grow p-10">
        <slot />
      </div>
      <layout-main-footer />
    </main>
  </AuthenticationWrapper>
</template>

<script setup lang="ts"></script>

<style scoped></style>
