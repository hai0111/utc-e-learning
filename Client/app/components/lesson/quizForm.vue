<template>
  <div class="grid grid-cols-2 gap-y-3 gap-x-9">
    <VValidation :rules="[onSummaryValidate]" />

    <VDivider thickness="1" class="col-span-2" />

    <info-item label="Quiz title">
      <v-text-field
        v-model="modelValue.quizzesRequest!.quizTitle"
        :rules="[required()]"
      />
    </info-item>

    <div></div>

    <info-item label="Total questions:">
      <div class="text-xl">
        {{
          String(
            modelValue.quizzesRequest?.quizQuestionsRequests?.length ?? 0
          ).padStart(2, "0")
        }}
      </div>
    </info-item>

    <info-item label="Total score:">
      <div class="font-normal text-xl">
        <span
          :class="[
            {
              'text-error': totalPoint != 100,
              'text-success': totalPoint == 100,
            },
          ]"
        >
          {{ totalPoint }}
        </span>
        /
        <span
          :class="[
            {
              'text-primary': totalPoint != 100,
              'text-success': totalPoint == 100,
            },
          ]"
        >
          100
        </span>

        <VIcon
          v-if="totalPoint != 100"
          icon="mdi-alert"
          color="red"
          size="20"
          class="ms-3"
        />
      </div>
    </info-item>

    <div class="flex items-center gap-3 col-span-2 text-nowrap text-lg mt-3">
      <VIcon icon="mdi-note-text-outline" />
      <span>Questions</span>
    </div>

    <div class="flex flex-col gap-4 col-span-2">
      <VExpansionPanels
        eager
        multiple
        variant="accordion"
        v-model="expandsControl"
        ref="questionsContainer"
      >
        <VExpansionPanel
          v-for="(question, index) in modelValue.quizzesRequest
            ?.quizQuestionsRequests"
          :key="question.id"
          class="flex flex-col gap-2"
          :value="question.id"
        >
          <VExpansionPanelTitle>
            <div class="flex items-center gap-3">
              <VIcon
                icon="mdi-drag-horizontal-variant"
                class="cursor-pointer mt-1 sort-question"
                size="24"
              />

              <span class="font-medium">
                <strong> [{{ question.questionType }}] </strong>

                <template v-if="question.questionText">
                  {{ question.questionText }}
                </template>

                <template v-else> Question {{ index + 1 }} </template>
              </span>

              <span class="text-primary">
                ({{ question.rawPoint }} points)
              </span>
            </div>
          </VExpansionPanelTitle>

          <VExpansionPanelText>
            <VTextField
              v-model="question.questionText"
              placeholder="Enter the question..."
              :rules="[required()]"
            />
            <div class="flex items-center justify-between mb-2">
              <info-item label="Raw point:" inline class="mb-5">
                <v-number-input
                  v-model="question.rawPoint"
                  control-variant="stacked"
                  density="compact"
                  :clearable="false"
                  :min="0"
                  :max="100"
                  :width="100"
                  hide-details
                  @blur="question.rawPoint ??= 0"
                />
              </info-item>

              <VBtn
                variant="outlined"
                prepend-icon="mdi-delete"
                color="red"
                @click="deleteQuestion(question.id!)"
              >
                Delete
              </VBtn>
            </div>

            <v-radio-group
              v-model="question.questionType"
              hide-details
              inline
              density="compact"
              color="primary"
              class="text-xs"
              @update:model-value="clearIsCorrectAnswer(question)"
            >
              <v-radio label="Single" :value="EQuestionType.SINGLE" />
              <v-radio
                label="Multiple"
                :value="EQuestionType.MULTIPLE"
                class="mx-3"
              />
              <v-radio label="Text" :value="EQuestionType.TEXT" />
            </v-radio-group>

            <template v-if="question.questionType !== EQuestionType.TEXT">
              <template v-if="question.optionsRequestList?.length">
                <div
                  class="grid items-start grid-cols-[auto_1fr_68px_68px] gap-y-1 gap-x-5"
                >
                  <div class="mb-3"></div>
                  <div class="mb-3"></div>
                  <div class="mb-3 font-black">Is correct</div>
                  <div class="mb-3"></div>
                </div>

                <div
                  :data-question-id="question.id"
                  ref="optionContainers"
                  class="flex flex-col gap-2"
                >
                  <div
                    v-for="(option, optionIndex) in question.optionsRequestList"
                    :key="option.id"
                    class="grid items-start grid-cols-[auto_1fr_68px_68px] gap-y-1 gap-x-5"
                  >
                    <VIcon
                      icon="mdi-drag-horizontal-variant"
                      class="cursor-pointer mt-3 sort-option"
                      size="18"
                    />

                    <VTextField
                      v-model="option.optionText"
                      variant="outlined"
                      density="compact"
                      placeholder="Enter option..."
                      :rules="[required()]"
                    />

                    <div class="flex justify-center">
                      <VCheckbox
                        hide-details
                        density="compact"
                        color="success"
                        v-model="option.isCorrect"
                        @update:model-value="
                          question.questionType === EQuestionType.SINGLE &&
                            clearOtherIsCorrect(optionIndex, question)
                        "
                      />
                    </div>

                    <VIcon
                      v-if="question.optionsRequestList.length > 2"
                      icon="mdi-delete"
                      color="red"
                      class="cursor-pointer mt-2"
                      size="24"
                      @click="onClickDeleteOption(optionIndex, question)"
                    />
                  </div>
                </div>
              </template>

              <VBtn
                class="mt-3"
                prepend-icon="mdi-plus-circle"
                color="success"
                variant="text"
                @click="onClickAddOption(question)"
              >
                New option
              </VBtn>
            </template>
          </VExpansionPanelText>
        </VExpansionPanel>
      </VExpansionPanels>

      <div>
        <VBtn
          prepend-icon="mdi-plus-circle"
          color="success"
          @click="onClickAddQuestion"
        >
          New question
        </VBtn>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { cloneDeep } from "lodash";
import Sortable from "sortablejs";
import type { VExpansionPanels } from "vuetify/components";
import { EFormType } from "~/constants/common";
import {
  ELessonTypes,
  EQuestionType,
  type ILessonForm,
  type IQuestionForm,
} from "~/types/lesson";

const props = withDefaults(
  defineProps<{
    modelValue: Partial<ILessonForm>;
    mode?: EFormType;
    disabled?: boolean;
  }>(),
  {
    mode: EFormType.CREATE,
  }
);

const emit = defineEmits<{
  (e: "update:modelValue", modelValue: Partial<ILessonForm>): void;
}>();

const onClickAddQuestion = () => {
  if (!props.modelValue.quizzesRequest) return;
  props.modelValue.quizzesRequest.quizQuestionsRequests ??= [];

  props.modelValue.quizzesRequest.quizQuestionsRequests.push({
    optionsRequestList: [
      {
        isCorrect: false,
        optionText: "",
        id: new Date().getTime() + 1,
      },

      {
        isCorrect: false,
        optionText: "",
        id: new Date().getTime() + 2,
      },
    ],
    questionType: EQuestionType.SINGLE,
    rawPoint: 0,
    id: new Date().getTime(),
  });
};

const onClickAddOption = (question: IQuestionForm) => {
  question.optionsRequestList ??= [];
  question.optionsRequestList.push({
    id: new Date().getTime(),
    isCorrect: false,
    optionText: "",
  });
};

const expandsControl = ref<number[]>([]);

const clearIsCorrectAnswer = (question: IQuestionForm) => {
  question.optionsRequestList?.forEach((item) => {
    item.isCorrect = false;
  });
};

const clearOtherIsCorrect = (optionIndex: number, question: IQuestionForm) => {
  question.optionsRequestList?.forEach((item, index) => {
    if (optionIndex !== index) item.isCorrect = false;
  });
};

const totalPoint = computed(
  () =>
    props.modelValue.quizzesRequest?.quizQuestionsRequests?.reduce(
      (acc, item) => acc + (item.rawPoint ?? 0),
      0
    ) ?? 0
);

const onClickDeleteOption = (optionIndex: number, question: IQuestionForm) => {
  question.optionsRequestList ??= [];
  question.optionsRequestList = question.optionsRequestList.filter(
    (_, index) => index !== optionIndex
  );
};

const questionsContainer = ref<InstanceType<typeof VExpansionPanels>>();

const sortableQuestions = ref<Sortable>();

onMounted(() => {
  if (!questionsContainer.value) return;

  sortableQuestions.value = Sortable.create(questionsContainer.value.$el, {
    handle: ".sort-question",
    animation: 150,
    ghostClass: "opacity-50",
    onEnd: ({ oldIndex, newIndex }) => {
      if (oldIndex == null || newIndex == null || oldIndex === newIndex) return;
      const list = cloneDeep(
        props.modelValue.quizzesRequest?.quizQuestionsRequests
      );

      if (!list) return;
      const moved = list.splice(oldIndex, 1)[0]!;
      list.splice(newIndex, 0, moved);
      props.modelValue.quizzesRequest!.quizQuestionsRequests = list;
    },
  });
});

onUnmounted(() => {
  sortableQuestions.value?.destroy();
});

const optionContainers = ref<HTMLDivElement[]>([]);
const optionSortables = ref<Sortable[]>([]);

watch(
  optionContainers,
  () => {
    for (const element of optionContainers.value) {
      optionSortables.value.push(
        Sortable.create(element, {
          handle: ".sort-option",
          animation: 150,
          ghostClass: "opacity-50",
          onEnd: ({ oldIndex, newIndex }) => {
            if (oldIndex == null || newIndex == null || oldIndex === newIndex)
              return;

            const question =
              props.modelValue.quizzesRequest?.quizQuestionsRequests?.find(
                (item) => item.id == element.dataset.questionId
              );

            if (!question) return;

            const options = cloneDeep(question.optionsRequestList ?? []);

            const moved = options.splice(oldIndex, 1)[0]!;

            options.splice(newIndex, 0, moved);

            question.optionsRequestList = options;
          },
        })
      );
    }
  },
  {
    deep: true,
  }
);

const deleteQuestion = (questionId: number) => {
  props.modelValue.quizzesRequest!.quizQuestionsRequests ??= [];
  props.modelValue.quizzesRequest!.quizQuestionsRequests =
    props.modelValue.quizzesRequest?.quizQuestionsRequests?.filter(
      (item) => item.id !== questionId
    );
};

const validateQuizForm = (values: Partial<ILessonForm>) => {
  if (values.type !== ELessonTypes.QUIZ) return { valid: true };

  if (!values.quizzesRequest?.quizQuestionsRequests?.length)
    return { valid: false, message: "Please enter at least one question?" };

  const totalScore =
    values.quizzesRequest?.quizQuestionsRequests?.reduce(
      (acc, item) => acc + (item.rawPoint ?? 0),
      0
    ) ?? 0;

  if (totalScore !== 100)
    return { valid: false, message: "Total score is invalid" };

  const isFullFillQuestionInfo =
    values.quizzesRequest?.quizQuestionsRequests?.every((item) => {
      let isEnterFullOptions = item.optionsRequestList?.every(
        (item) => item.optionText
      );

      let isValidIfTypeOfQuestionNotText =
        item.questionType === EQuestionType.TEXT || isEnterFullOptions;

      return (
        item.questionText && item.rawPoint && isValidIfTypeOfQuestionNotText
      );
    });

  if (!isFullFillQuestionInfo)
    return {
      valid: false,
      message: "Please enter complete information for the questions",
    };

  const hasAtLeastOneCorrectAnswer =
    values.quizzesRequest?.quizQuestionsRequests?.every((item) => {
      return (
        item.questionType === EQuestionType.TEXT ||
        item.optionsRequestList?.some((item) => item.isCorrect)
      );
    });

  if (!hasAtLeastOneCorrectAnswer)
    return {
      valid: false,
      message: "There must exist at least one correct question",
    };

  return { valid: true };
};

const isMounted = ref(false);
onMounted(() => {
  isMounted.value = true;
});

const cleanDataBeforePosting = (values: Partial<ILessonForm>) => {
  values.quizzesRequest?.quizQuestionsRequests?.forEach((item, index) => {
    item.orderIndex = index;
    if (item.questionType === EQuestionType.TEXT)
      item.optionsRequestList = undefined;
    else
      item.optionsRequestList?.forEach((option, optionIndex) => {
        option.orderIndex = optionIndex;
      });
  });
};

const onSummaryValidate = () => {
  if (!isMounted.value) return true;
  const { valid, message } = validateQuizForm(props.modelValue);

  if (!valid) {
    toastError(message ?? "");
    return message ?? "";
  }

  cleanDataBeforePosting(props.modelValue);

  return valid as true;
};
</script>

<style scoped></style>
