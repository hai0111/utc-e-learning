<template>
  <div class="grid grid-cols-[1fr_400px] gap-3">
    <v-card>
      <v-card-title class="text-3xl font-medium pt-4">
        Ứng dụng phần mềm
      </v-card-title>

      <v-card-subtitle> Last updated: 01/11/2025 </v-card-subtitle>

      <v-card-text>
        Lorem ipsum dolor sit amet consectetur adipisicing elit. Praesentium non
        consequuntur voluptatum alias dolorem, dolor maiores debitis incidunt
        rem, rerum laborum ea ullam. Sint, perspiciatis? Suscipit ex placeat
        repudiandae provident nam error totam quis. Reprehenderit aspernatur
        voluptatibus aperiam corporis tenetur quo esse quidem magnam autem
        inventore pariatur dolores animi non temporibus perspiciatis corrupti
        quae sapiente similique, vitae eligendi tempore eveniet! Quis, officia
        incidunt, ullam libero, asperiores soluta sunt itaque enim vitae hic
        illo pariatur ducimus neque magnam necessitatibus dicta sequi magni
        tempora nulla impedit! Inventore error numquam fugit sunt accusamus
        nihil excepturi, aliquam vitae pariatur ab in voluptate tenetur maiores
        voluptas animi doloremque dolor delectus debitis odit ad nostrum
        architecto quaerat. Eligendi perspiciatis neque delectus veritatis
        voluptas magnam, tempora mollitia.
      </v-card-text>

      <v-divider />

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
      </v-card-text>

      <v-divider />

      <v-card-title class="font-medium text-2xl flex items-center gap-2 mt-3">
        <v-icon icon="mdi-star" color="#fece00" />

        4.8 Course rating
      </v-card-title>

      <v-card-subtitle> 284,123 reviews </v-card-subtitle>

      <v-card-text>
        <div class="flex flex-col gap-3">
          <template v-for="i in 5">
            <review />
            <v-divider v-if="i !== 5" />
          </template>
        </div>

        <div class="flex justify-center mt-10">
          <v-pagination
            :length="4"
            next-icon="mdi-menu-right"
            prev-icon="mdi-menu-left"
          />
        </div>
      </v-card-text>
    </v-card>

    <div>
      <div class="grid items-start gap-3">
        <v-card>
          <v-card-text
            class="flex flex-col items-center justify-center gap-4 aspect-square"
          >
            <v-progress-circular
              :model-value="60"
              :rotate="360"
              :size="200"
              :width="30"
              color="teal"
            >
              <template v-slot:default>
                <span class="font-bold text-3xl">{{ 60 }} %</span>
              </template>
            </v-progress-circular>

            <div class="text-xl font-medium text-center">
              38/158
              <br />
              <span class="font-thin text-base">Lessons</span>
            </div>
          </v-card-text>
        </v-card>
      </div>
      <v-btn block class="col-span-2 bg-blue-600 text-white mt-3" size="40">
        Start learning
      </v-btn>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped></style>
