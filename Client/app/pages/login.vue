<template>
  <div class="grid grid-cols-[1fr_40%] justify-end h-screen bg-blue-100">
    <nuxt-img src="/login_bg.png" class="object-cover h-screen" />

    <div class="flex flex-col justify-center gap-20 px-5 bg-white">
      <div class="text-5xl font-thin">Login</div>

      <v-form class="w-full max-w-[600px]">
        <label>
          Username

          <v-text-field
            class="mt-1"
            v-model="formValues.username"
            variant="outlined"
            density="compact"
          />
        </label>

        <label>
          Password
          <v-text-field
            v-model="formValues.password"
            class="mt-1"
            type="password"
            variant="outlined"
            density="compact"
          />
        </label>

        <v-btn variant="outlined" color="primary" block append-icon="mdi-login"
          >Login</v-btn
        >
      </v-form>
    </div>
  </div>
</template>

<script setup lang="ts">
const formValues = ref({
  username: "",
  password: "",
});
</script>

<style scoped></style>
