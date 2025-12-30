<template>
  <div
    class="grid grid-cols-[400px_1fr] grid-rows-[auto_1fr_auto] h-[calc(100vh-80px)] rounded-xl"
  >
    <VSheet
      width="100%"
      color="#fff"
      class="col-span-2 py-3 px-5 border-b flex items-center justify-between"
    >
      <span class="text-xl font-bold">
        {{ dataDetail.quizTitle }}
      </span>

      <span class="font-bold text-orange-600"> Remaining: 00:59:00 </span>
    </VSheet>

    <VSheet width="100%" color="#fff" class="border-r overflow-auto">
      <v-list v-model:selected="selected" return-object class="py-0" mandatory>
        <v-list-item
          v-for="(item, index) in dataDetail.quizQuestionsRequests"
          :key="item.id"
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
      <div class="grow">
        <div class="flex justify-between">
          <div>
            Question
            {{
              dataDetail.quizQuestionsRequests.findIndex(
                (item) => itemSelected?.id === item.id
              ) + 1
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
          class="max-w-[500px] mt-4"
          :mandatory="itemSelected?.questionType === EQuestionType.SINGLE"
          :select-strategy="
            itemSelected?.questionType === EQuestionType.MULTIPLE
              ? 'leaf'
              : 'single-leaf'
          "
        >
          <v-list-item
            v-for="item in itemSelected?.optionsRequestList"
            :key="item.id"
            :value="item"
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

        <VTextarea v-else placeholder="Điền câu trả lời của bạn" class="mt-8" />
      </div>

      <div>
        <VPagination
          :length="dataDetail.quizQuestionsRequests.length"
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
        Hoàn thành
      </v-btn>
    </div>

    <VDialog v-model="isOpenConfirm" :width="480">
      <VCard>
        <VCardTitle> Xác nhận </VCardTitle>
        <VCardText> Bạn chắc chắn muốn nộp bài chứ? </VCardText>
        <VCardActions>
          <span class="text-orange-600 text-sm ms-3"> 00:59:00 </span>
          <VSpacer />
          <VBtn @click="isOpenConfirm = false"> Tiếp tục làm </VBtn>
          <VBtn color="success" variant="flat"> Submit </VBtn>
        </VCardActions>
      </VCard>
    </VDialog>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: "quiz-layout",
});

import { EQuestionType } from "~/types/lesson";

const dataDetail = {
  quizQuestionsRequests: [
    {
      id: 1767105523285,
      questionType: EQuestionType.SINGLE,
      isTouched: false,
      markAsLater: false,
      questionText: "Thủ đô của Nhật Bản là gì?",
      rawPoint: 5,
      orderIndex: 0,
      optionsRequestList: [
        {
          id: 1767105523286,
          optionText: "Osaka",
          isCorrect: false,
          orderIndex: 0,
        },
        { optionText: "Kyoto", isCorrect: false, orderIndex: 1 },
        {
          id: 1767105538082,
          optionText: "Tokyo",
          isCorrect: true,
          orderIndex: 2,
        },
        {
          id: 1767105541314,
          optionText: "Nagoya",
          isCorrect: false,
          orderIndex: 3,
        },
      ],
    },
    {
      id: 1767105544546,
      questionType: EQuestionType.MULTIPLE,
      isTouched: false,
      markAsLater: false,
      questionText: "Ngôn ngữ nào sau đây là ngôn ngữ lập trình?",
      rawPoint: 10,
      orderIndex: 1,
      optionsRequestList: [
        {
          id: 1767105544547,
          optionText: "JavaScript",
          isCorrect: true,
          orderIndex: 0,
        },
        {
          id: 1767105544548,
          optionText: "HTML",
          isCorrect: false,
          orderIndex: 1,
        },
        {
          id: 1767105563429,
          optionText: "Python",
          isCorrect: true,
          orderIndex: 2,
        },
        {
          id: 1767105566906,
          optionText: "CSS",
          isCorrect: false,
          orderIndex: 3,
        },
      ],
    },
    {
      id: 1767105573277,
      questionType: EQuestionType.TEXT,
      isTouched: false,
      markAsLater: false,
      questionText: "Hãy nêu ngắn gọn React dùng để làm gì?",
      rawPoint: 10,
      orderIndex: 2,
    },
    {
      id: 1767105600001,
      questionType: EQuestionType.SINGLE,
      isTouched: false,
      markAsLater: false,
      questionText: "HTTP status code nào biểu thị thành công?",
      rawPoint: 5,
      orderIndex: 3,
      optionsRequestList: [
        { optionText: "200", isCorrect: true, orderIndex: 0 },
        { optionText: "404", isCorrect: false, orderIndex: 1 },
        { optionText: "500", isCorrect: false, orderIndex: 2 },
        { optionText: "301", isCorrect: false, orderIndex: 3 },
      ],
    },
    {
      id: 1767105600002,
      questionType: EQuestionType.MULTIPLE,
      isTouched: false,
      markAsLater: false,
      questionText: "Đâu là state management library?",
      rawPoint: 10,
      orderIndex: 4,
      optionsRequestList: [
        { optionText: "Redux", isCorrect: true, orderIndex: 0 },
        { optionText: "Pinia", isCorrect: true, orderIndex: 1 },
        { optionText: "Axios", isCorrect: false, orderIndex: 2 },
        { optionText: "Lodash", isCorrect: false, orderIndex: 3 },
      ],
    },
    {
      id: 1767105600003,
      questionType: EQuestionType.TEXT,
      isTouched: false,
      markAsLater: false,
      questionText: "REST API là gì?",
      rawPoint: 10,
      orderIndex: 5,
    },
    {
      id: 1767105600004,
      questionType: EQuestionType.SINGLE,
      isTouched: false,
      markAsLater: false,
      questionText: "Hook nào dùng để quản lý state trong React?",
      rawPoint: 5,
      orderIndex: 6,
      optionsRequestList: [
        { optionText: "useState", isCorrect: true, orderIndex: 0 },
        { optionText: "useEffect", isCorrect: false, orderIndex: 1 },
        { optionText: "useMemo", isCorrect: false, orderIndex: 2 },
        { optionText: "useRef", isCorrect: false, orderIndex: 3 },
      ],
    },
    {
      id: 1767105600005,
      questionType: EQuestionType.MULTIPLE,
      isTouched: false,
      markAsLater: false,
      questionText: "Đâu là CSS framework?",
      rawPoint: 10,
      orderIndex: 7,
      optionsRequestList: [
        { optionText: "Tailwind CSS", isCorrect: true, orderIndex: 0 },
        { optionText: "Bootstrap", isCorrect: true, orderIndex: 1 },
        { optionText: "Vue", isCorrect: false, orderIndex: 2 },
        { optionText: "React", isCorrect: false, orderIndex: 3 },
      ],
    },
    {
      id: 1767105600006,
      questionType: EQuestionType.TEXT,
      isTouched: false,
      markAsLater: false,
      questionText: "Sự khác nhau giữa var, let và const?",
      rawPoint: 10,
      orderIndex: 8,
    },
    {
      id: 1767105600007,
      questionType: EQuestionType.SINGLE,
      isTouched: false,
      markAsLater: false,
      questionText: "Vite chủ yếu dùng để làm gì?",
      rawPoint: 5,
      orderIndex: 9,
      optionsRequestList: [
        { optionText: "Build tool", isCorrect: true, orderIndex: 0 },
        { optionText: "Database", isCorrect: false, orderIndex: 1 },
        { optionText: "UI library", isCorrect: false, orderIndex: 2 },
        { optionText: "Testing framework", isCorrect: false, orderIndex: 3 },
      ],
    },
    {
      id: 1767105600008,
      questionType: EQuestionType.MULTIPLE,
      isTouched: false,
      markAsLater: false,
      questionText: "Đâu là HTTP methods hợp lệ?",
      rawPoint: 10,
      orderIndex: 10,
      optionsRequestList: [
        { optionText: "GET", isCorrect: true, orderIndex: 0 },
        { optionText: "POST", isCorrect: true, orderIndex: 1 },
        { optionText: "FETCH", isCorrect: false, orderIndex: 2 },
        { optionText: "SEND", isCorrect: false, orderIndex: 3 },
      ],
    },
    {
      id: 1767105600009,
      questionType: EQuestionType.SINGLE,
      isTouched: false,
      markAsLater: false,
      questionText: "TypeScript là gì?",
      rawPoint: 5,
      orderIndex: 11,
      optionsRequestList: [
        {
          optionText: "Superset của JavaScript",
          isCorrect: true,
          orderIndex: 0,
        },
        { optionText: "Framework backend", isCorrect: false, orderIndex: 1 },
        { optionText: "Database", isCorrect: false, orderIndex: 2 },
        { optionText: "CSS preprocessor", isCorrect: false, orderIndex: 3 },
      ],
    },
  ],
  quizTitle: "Bài kiểm tra cuối cùng",
};

const selected = ref([dataDetail.quizQuestionsRequests[0]]);
const itemSelected = computed(() => selected.value[0] ?? null);

const questionIndex = ref(1);

watch(questionIndex, (val) => {
  selected.value = [dataDetail.quizQuestionsRequests[val - 1]];
});

watch(
  selected,
  (val) => {
    const selectedItem = val[0];
    if (!selectedItem) return;
    selectedItem.isTouched = true;
    questionIndex.value =
      dataDetail.quizQuestionsRequests.findIndex(
        (item) => item.id === selectedItem.id
      ) + 1;
  },
  {
    immediate: true,
  }
);

const isOpenConfirm = ref(false);
</script>

<style scoped></style>
