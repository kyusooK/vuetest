<script setup>
import {
  VerticalNavLink,
  VerticalNavSectionTitle,
} from '@layouts'
</script>

<template>
    <ul>
        <VerticalNavSectionTitle :item="{ heading: 'Order' }" />
        <VerticalNavLink
            :item="{
                title: 'Order',
                to: '/orders/orders',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '',
                to: '//',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: 'Inventory' }" />
        <VerticalNavLink
            :item="{
                title: 'Inventory',
                to: '/inventories/inventories',
            }"
        />
        <VerticalNavLink
            :item="{
                title: '',
                to: '//',
            }"
        />
    </ul>
</template>
