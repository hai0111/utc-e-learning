<template>
  <div class="grid grid-cols-[1fr_600px] gap-3">
    <v-card>
      <v-card-text>
        <div class="text-3xl font-medium">Ứng dụng phần mềm</div>
        <div class="text-xs mt-1">Last updated: 01/11/2025</div>

        <div class="mt-8">
          Lorem ipsum dolor sit amet consectetur adipisicing elit. Praesentium
          non consequuntur voluptatum alias dolorem, dolor maiores debitis
          incidunt rem, rerum laborum ea ullam. Sint, perspiciatis? Suscipit ex
          placeat repudiandae provident nam error totam quis. Reprehenderit
          aspernatur voluptatibus aperiam corporis tenetur quo esse quidem
          magnam autem inventore pariatur dolores animi non temporibus
          perspiciatis corrupti quae sapiente similique, vitae eligendi tempore
          eveniet! Quis, officia incidunt, ullam libero, asperiores soluta sunt
          itaque enim vitae hic illo pariatur ducimus neque magnam
          necessitatibus dicta sequi magni tempora nulla impedit! Inventore
          error numquam fugit sunt accusamus nihil excepturi, aliquam vitae
          pariatur ab in voluptate tenetur maiores voluptas animi doloremque
          dolor delectus debitis odit ad nostrum architecto quaerat. Eligendi
          perspiciatis neque delectus veritatis voluptas magnam, tempora
          mollitia.
        </div>
      </v-card-text>
    </v-card>

    <div>
      <div class="grid grid-cols-2 items-start gap-3">
        <v-card>
          <v-card-text
            class="flex flex-col items-center justify-center gap-4 aspect-square"
          >
            <div
              class="text-7xl font-extrabold text-blue-800 h-[120px] leading-[120px]"
            >
              300
            </div>
            <div class="text-xl font-medium">Lessons</div>
          </v-card-text>
        </v-card>
        <v-card>
          <v-card-text
            class="flex flex-col items-center justify-center gap-4 aspect-square"
          >
            <v-progress-circular
              :model-value="60"
              :rotate="360"
              :size="120"
              :width="15"
              color="teal"
            >
              <template v-slot:default> {{ 60 }} % </template>
            </v-progress-circular>

            <div class="text-xl font-medium">Progress</div>
          </v-card-text>
        </v-card>
      </div>
      <v-btn block class="col-span-2 bg-blue-600 text-white mt-3" size="40">
        Start learning
      </v-btn>
    </div>

    <v-card>
      <v-card-title class="font-normal text-base"> Instructor </v-card-title>
      <v-card-text>
        <nuxt-link to="/" class="font-medium text-blue-500 text-base">
          Nguyễn Đức Dư
        </nuxt-link>

        <div>Ứng dụng phần mềm Instructor</div>

        <div class="flex items-start gap-3 mt-3">
          <nuxt-img
            src="/vietnam.jpg"
            width="100"
            class="rounded-circle"
            alt=""
          />

          <div class="grid grid-cols-[32px_1fr] gap-y-1">
            <v-icon icon="mdi-star-outline" />
            <div>4.6 Rating</div>

            <v-icon icon="mdi-license" />
            <div>584,287 Reviews</div>

            <v-icon icon="mdi-account-multiple-outline " />
            <div>1,935,274 Students</div>

            <v-icon icon="mdi-play-circle-outline" />
            <div>30 Courses</div>
          </div>
        </div>

        <div class="mt-5">
          Lorem ipsum dolor sit amet consectetur, adipisicing elit. Adipisci
          eius rerum eveniet velit? Ad iure id esse eius vitae praesentium
          molestias quibusdam ipsum. Iste totam enim molestiae minus, harum
          eveniet tempora possimus minima accusantium dolor error a doloribus,
          vel facilis?
        </div>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped></style>
