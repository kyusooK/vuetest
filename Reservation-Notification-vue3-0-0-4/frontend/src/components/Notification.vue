<template>
    <div>
        <String label="NotificationId" v-model="value.notificationId" :editMode="editMode"/>
        <String label="UserId" v-model="value.userId" :editMode="editMode"/>
        <String label="TaskId" v-model="value.taskId" :editMode="editMode"/>
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
    name: 'Notification',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "notifications",
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
