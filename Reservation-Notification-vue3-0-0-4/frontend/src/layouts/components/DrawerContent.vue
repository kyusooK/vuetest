<script setup>
import {
  VerticalNavLink,
  VerticalNavSectionTitle,
} from '@layouts'
</script>

<template>
    <ul>
        <VerticalNavSectionTitle :item="{ heading: 'Reservation' }" />
        <VerticalNavLink
            :item="{
                title: 'Reservation',
                to: '/reservations/reservations',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: 'Notification' }" />
        <VerticalNavLink
            :item="{
                title: 'Notification',
                to: '/notifications/notifications',
            }"
        />
    </ul>
</template>
