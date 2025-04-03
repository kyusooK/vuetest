<template>
    <div>
        <String label="TaskId" v-model="value.taskId" :editMode="editMode"/>
        <String label="UserId" v-model="value.userId" :editMode="editMode"/>
        <String label="Title" v-model="value.title" :editMode="editMode"/>
        <String label="Description" v-model="value.description" :editMode="editMode"/>
        <Date label="DueDate" v-model="value.dueDate" :editMode="editMode"/>
        <v-divider class="border-opacity-50 my-divider my-2"></v-divider>
        <v-row class="ma-0 pa-0">
            <v-spacer></v-spacer>
            <v-btn
                width="64px"
                color="primary"
                @click="save"
            >
                저장
            </v-btn>
        </v-row>
    </div>
</template>


<script>

import BaseEntity from './base-ui/BaseEntity.vue'

export default {
    name: 'Reservation',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "reservations",
        value: {
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
    },
}
</script>
