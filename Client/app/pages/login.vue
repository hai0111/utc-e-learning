<template>
  <div class="grid grid-cols-[1fr_40%] justify-end h-screen bg-blue-100">
    <div>
      <nuxt-img src="/login_bg.png" class="object-cover h-screen w-full" />
    </div>

    <div class="flex flex-col justify-center gap-20 px-5 bg-white">
      <div class="text-5xl font-thin">Login</div>

      <v-form class="w-full max-w-[600px]">
        <label>
          Username

          <v-text-field
            v-model="formValues.email"
            class="mt-1"
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

        <v-btn
          variant="outlined"
          color="primary"
          block
          append-icon="mdi-login"
          @click="submit"
        >
          Login
        </v-btn>
      </v-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginService from "~/services/login.service";
import type { ILoginBody } from "~/types/auth";

definePageMeta({
  layout: false,
});

const formValues = ref<ILoginBody>({
  email: "",
  password: "",
});

const submit = async () => {
  try {
    const res = await LoginService.submit(formValues.value);

    // TODO: Handle API
    console.log(res);
  } catch (err) {
    console.error(err);
  }
};
</script>

<style scoped></style>
