<template>
  <div>
    <div class="flex items-center gap-3">
      <nuxt-img src="/vietnam.jpg" width="36" class="rounded-circle" alt="" />

      <div>
        <div class="font-bold">Marius M.</div>
        <div class="flex items-center gap-3 mt-0.5">
          <rating :model-value="3.6" :size="18" />

          <strong>a month ago</strong>
        </div>
      </div>
    </div>

    <div class="mt-3">
      This course was amazing! I really gained a lot of information and I feel
      very confident in my ability to continue to build on what i've learned.
      The YelpCamp project is incredible, it allowed me to get a feel for what
      can possibly go into a basic web application. Colt is a great teacher,
      knowledgeable and enjoyable to listen to! Thank you
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped></style>
