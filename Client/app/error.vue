<template>
  <div
    class="page_404 flex text-center flex-col gap-3 pb-[200px]! justify-center items-center h-screen"
  >
    <h1 class="text-8xl">{{ error?.statusCode }}</h1>

    <div class="four_zero_four_bg"></div>

    <div class="contant_box_404">
      <h3 class="h2 mb-5 text-xl">{{ error?.message }}</h3>

      <a href="/">
        <v-btn class="link_404" color="primary"> Quay lại trang chủ </v-btn>
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { NuxtError } from "#app";

const props = defineProps({
  error: Object as () => NuxtError,
});
</script>

<style scoped>
.page_404 img {
  width: 100%;
}

.four_zero_four_bg {
  background-image: url(https://cdn.dribbble.com/users/285475/screenshots/2083086/dribbble_1.gif);
  height: 400px;
  width: 100%;
  background-position: center;
}
</style>
