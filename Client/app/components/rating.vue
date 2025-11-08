<template>
  <div class="flex items-center flex-nowrap">
    <v-icon v-for="i in 5" icon="mdi-star" :style="getStyles(i)" :size="size" />
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  modelValue: number;
  size?: number;
}>();
const getStyles = (i: number) => {
  if (i <= props.modelValue) {
    return {
      color: "#fece00",
    };
  }

  if (props.modelValue - i + 1 > 0) {
    return {
      "background-image": `linear-gradient(to right, #fece00 ${Math.floor(
        (props.modelValue % 1) * 100
      )}%, #ccc ${Math.floor((props.modelValue % 1) * 100)}%)`,
      "-webkit-background-clip": "text",
      "-webkit-text-fill-color": "transparent",
    };
  }

  return {
    color: "#ccc",
  };
};
</script>
