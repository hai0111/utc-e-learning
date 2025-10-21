<template>
  <header class="flex items-center gap-3 p-4 bg-white">
    <nuxt-img src="/logo.png" width="50" alt="" />

    <h1 class="text-2xl font-extrabold grow">My E-Learning</h1>

    <span>Nguyễn Văn A</span>

    <v-menu transition="slide-y-transition" :offset="10">
      <template #activator="{ props }">
        <nuxt-img
          src="/vietnam.jpg"
          width="50"
          class="rounded-circle"
          alt=""
          v-bind="props"
        />
      </template>

      <v-list density="compact" class="py-0">
        <v-list-item link to="/login" append-icon="mdi-logout">
          <v-list-item-title>Logout</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-menu>
  </header>
</template>

<script setup lang="ts"></script>

<style scoped></style>
