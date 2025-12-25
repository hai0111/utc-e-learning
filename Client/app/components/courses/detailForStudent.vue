<template>
  <div class="grid grid-cols-[1fr_400px] gap-3">
    <v-card>
      <v-card-title class="text-3xl font-medium pt-4">
        {{ data?.title }}
      </v-card-title>

      <v-card-subtitle>
        Last updated: {{ dayjs(data?.updatedAt).format("DD/MM/YYYY") }}
      </v-card-subtitle>

      <v-card-text>
        {{ data?.description }}
      </v-card-text>

      <v-divider />

      <v-card-text>
        <nuxt-link to="/" class="font-medium text-blue-500 text-base">
          {{ data?.instructor }}
        </nuxt-link>

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
              :model-value="data?.progressPercentage ?? 0"
              :rotate="360"
              :size="200"
              :width="30"
              color="teal"
            >
              <template v-slot:default>
                <span class="font-bold text-3xl"
                  >{{ data?.progressPercentage?.toFixed() ?? 0 }} %</span
                >
              </template>
            </v-progress-circular>
          </v-card-text>
        </v-card>
      </div>
      <v-btn
        block
        class="col-span-2 bg-blue-600 text-white mt-3"
        size="40"
        @click="$router.push(`/courses/${$route.params.id}/learn`)"
      >
        Start learning
      </v-btn>
    </div>
  </div>
</template>

<script setup lang="ts">
import dayjs from "dayjs";
import CourseService from "~/services/course.service";

const { params } = useRoute();

const { data } = useAsyncData(`course/${params.id as string}`, () =>
  CourseService.detail((params.id as string) ?? "")
);
</script>

<style scoped></style>
