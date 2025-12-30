<template>
  <div
    v-if="!isSubmitSuccessFully && !isLoading"
    class="grid grid-cols-[400px_1fr] grid-rows-[auto_1fr_auto] h-[calc(100vh-80px)] rounded-xl"
  >
    <VSheet
      width="100%"
      color="#fff"
      class="col-span-2 py-3 px-5 border-b flex items-center justify-between"
    >
      <span class="text-xl font-bold">
        {{ dataDetail?.quizTitle }}
      </span>

      <span class="font-bold text-orange-600"> Remaining: 00:59:00 </span>
    </VSheet>

    <VSheet width="100%" color="#fff" class="border-r overflow-auto">
      <v-list v-model:selected="selected" return-object class="py-0" mandatory>
        <v-list-item
          v-for="(item, index) in dataDetail?.quizQuestionsResponses"
          :key="item.questionId"
          :value="item"
          active-class="bg-grey-lighten-4"
          :class="[
            'py-5',
            {
              'border-l-8': item.isTouched || item.markAsLater,
              'border-[#fe8948]': item.markAsLater,
              'border-blue-600': item.isTouched && !item.markAsLater,
            },
          ]"
        >
          <v-list-item-subtitle class="mb-1 text-high-emphasis opacity-100">
            <div class="flex justify-between">
              <span>Question {{ index + 1 }} </span>
              <div>
                <span>
                  {{
                    item.questionType
                      .toLowerCase()
                      .replace(/^\w/, (a) => a.toUpperCase())
                  }}
                </span>
                <span class="text-primary"> ({{ item.rawPoint }} points) </span>
              </div>
            </div>
          </v-list-item-subtitle>

          <v-list-item-title class="mt-2">
            {{ item.questionText }}
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </VSheet>

    <VSheet width="100%" color="#fff" class="p-6 flex flex-col">
      <div class="grow" v-if="itemSelected">
        <div class="flex justify-between">
          <div>
            Question
            {{
              (dataDetail?.quizQuestionsResponses?.findIndex(
                (item) => itemSelected?.questionId === item.questionId
              ) ?? 0) + 1
            }}

            <span class="text-primary ms-2">
              ({{ itemSelected?.rawPoint }} points)
            </span>
          </div>

          <VIcon
            v-if="!itemSelected?.markAsLater"
            color="#fe8948"
            icon="mdi-bookmark-outline"
            class="cursor-pointer"
            size="30"
            @click="
              itemSelected!.markAsLater = true;
              selected = [...selected];
            "
          />
          <VIcon
            v-else
            color="#fe8948"
            icon="mdi-bookmark"
            class="cursor-pointer"
            size="30"
            @click="
              itemSelected!.markAsLater = false;
              selected = [...selected];
            "
          />
        </div>

        <div class="mt-3 font-bold">
          {{ itemSelected?.questionText }}
        </div>

        <v-list
          v-if="
            [EQuestionType.SINGLE, EQuestionType.MULTIPLE].includes(
              itemSelected?.questionType as EQuestionType
            )
          "
          v-model:selected="itemSelected!.selectedOptionIds"
          class="max-w-[500px] mt-4"
          :mandatory="itemSelected?.questionType === EQuestionType.SINGLE"
          :select-strategy="
            itemSelected?.questionType === EQuestionType.MULTIPLE
              ? 'leaf'
              : 'single-leaf'
          "
        >
          <v-list-item
            v-for="item in itemSelected?.optionsResponseList"
            :key="item.optionId"
            :value="item.optionId"
            active-class="text-primary"
            class="py-2"
          >
            <v-list-item-title>
              {{ item.optionText }}
            </v-list-item-title>

            <template #append="{ isSelected, select }">
              <v-list-item-action class="flex-column align-end">
                <VRadio
                  v-if="itemSelected?.questionType === EQuestionType.SINGLE"
                  :model-value="isSelected"
                  color="primary"
                  readonly
                  @click="select"
                />
                <VCheckbox
                  v-else
                  :model-value="isSelected"
                  color="primary"
                  hide-details
                  density="compact"
                  readonly
                  @click="select(!isSelected)"
                />
              </v-list-item-action>
            </template>
          </v-list-item>
        </v-list>

        <VTextarea
          v-else
          v-model="itemSelected.textAnswer"
          placeholder="Điền câu trả lời của bạn"
          class="mt-8"
        />
      </div>

      <div>
        <VPagination
          :length="dataDetail?.quizQuestionsResponses?.length"
          :total-visible="10"
          v-model="questionIndex"
        >
          <template #item="{ page, props }">
            <v-btn v-bind="props" variant="text">
              {{ page }}
            </v-btn>
          </template>
        </VPagination>
      </div>
    </VSheet>

    <div class="flex justify-end mt-3 col-span-2">
      <v-btn
        color="success"
        class="rounded-pill"
        :height="48"
        :width="180"
        @click="isOpenConfirm = true"
      >
        Finish
      </v-btn>
    </div>

    <VDialog v-model="isOpenConfirm" :width="480">
      <VCard>
        <VCardTitle> Confirmation </VCardTitle>
        <VCardText> Are you sure you want to submit your exam? </VCardText>
        <VCardActions>
          <span class="text-orange-600 text-sm ms-3"> 00:59:00 </span>
          <VSpacer />
          <VBtn @click="isOpenConfirm = false"> Continue </VBtn>
          <VBtn color="success" variant="flat" @click="onSubmit"> Submit </VBtn>
        </VCardActions>
      </VCard>
    </VDialog>
  </div>

  <div
    v-else-if="isLoading"
    class="h-full flex flex-col items-center justify-center"
  >
    <v-progress-circular :size="70" :width="7" color="primary" indeterminate />
  </div>

  <div v-else class="h-full flex flex-col items-center justify-center gap-5">
    <span class="text-2xl">Submission Successful 🎉</span>
    <v-btn to="/" color="primary">Return to Home</v-btn>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: "quiz-layout",
});

import { DEFAULT_MESSAGES } from "~/constants/messages";
import ExamService from "~/services/exam.service";
import { EQuestionType } from "~/types/lesson";
import type { IAnswer } from "~/types/quiz";

const { params, query } = useRoute();

const { data: dataDetail } = useAsyncData(
  `course/${params.id as string}`,
  () => ExamService.detail((params.id as string) ?? ""),
  { default: () => null }
);

const selected = ref([dataDetail.value?.quizQuestionsResponses?.[0]]);

watch(dataDetail, () => {
  selected.value = [dataDetail.value?.quizQuestionsResponses?.[0]];
});

const itemSelected = computed(() => selected.value[0] ?? null);

const questionIndex = ref(1);

watch(questionIndex, (val) => {
  selected.value = [dataDetail.value?.quizQuestionsResponses?.[val - 1]];
});

watch(
  selected,
  (val) => {
    const selectedItem = val[0];
    if (!selectedItem) return;
    selectedItem.isTouched = true;
    questionIndex.value =
      (dataDetail.value?.quizQuestionsResponses?.findIndex(
        (item) => item.questionId === selectedItem.questionId
      ) ?? 0) + 1;
  },
  {
    immediate: true,
  }
);

const isOpenConfirm = ref(false);

const isSubmitSuccessFully = ref(false);
const isLoading = ref(false);

const onSubmit = async () => {
  isLoading.value = true;
  const answers = dataDetail.value?.quizQuestionsResponses.map(
    (item) =>
      ({
        questionId: item.questionId,
        selectedOptionIds: item.selectedOptionIds,
        textAnswer: item.textAnswer,
      } as IAnswer)
  );
  try {
    await ExamService.submit(params.id as string, {
      studentAnswers: answers ?? [],
    });

    isSubmitSuccessFully.value = true;

    if (query.back) await navigateTo((query.back as string) ?? "");

    toastSuccess(DEFAULT_MESSAGES.apiSuccess);
  } catch (err) {
    toastError(DEFAULT_MESSAGES.apiError);
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped></style>
