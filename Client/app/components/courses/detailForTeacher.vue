<template>
  <div class="grid grid-cols-[2fr_1fr] items-start gap-3">
    <div>
      <v-card>
        <v-card-title class="flex justify-end">
          <v-btn variant="text" color="primary" append-icon="mdi-pencil">
            Edit
          </v-btn>
        </v-card-title>
        <v-card-text>
          <div class="grid grid-cols-2 gap-4 mt-5">
            <info-item label="Title"> Ứng dụng phần mềm </info-item>

            <info-item label="Status"> Active </info-item>

            <info-item label="Description" class="col-span-2">
              Lorem ipsum dolor sit amet consectetur adipisicing elit.
              Praesentium non consequuntur voluptatum alias dolorem, dolor
              maiores debitis incidunt rem, rerum laborum ea ullam. Sint,
              perspiciatis? Suscipit ex placeat repudiandae provident nam error
              totam quis. Reprehenderit aspernatur voluptatibus aperiam corporis
              tenetur quo esse quidem magnam autem inventore pariatur dolores
              animi non temporibus perspiciatis corrupti quae sapiente
              similique, vitae eligendi tempore eveniet! Quis, officia incidunt,
              ullam libero, asperiores soluta sunt itaque enim vitae hic illo
              pariatur ducimus neque magnam necessitatibus dicta sequi magni
              tempora nulla impedit! Inventore error numquam fugit sunt
              accusamus nihil excepturi, aliquam vitae pariatur ab in voluptate
              tenetur maiores voluptas animi doloremque dolor delectus debitis
              odit ad nostrum architecto quaerat. Eligendi perspiciatis neque
              delectus veritatis voluptas magnam, tempora
            </info-item>
          </div>
        </v-card-text>
      </v-card>

      <courses-lessons-for-teacher />
    </div>

    <courses-students-for-teacher />
  </div>
</template>

<script setup lang="ts"></script>

<style scoped></style>
