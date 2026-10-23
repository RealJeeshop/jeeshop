<template>
    <v-tab :to="to">
        <v-icon v-if="icon" :icon="icon"></v-icon>
        {{text}}
    </v-tab>
</template>

<script>
    export default {
        name: "SideNavItem",
        props: {
            text: String,
            icon: String,
            to: String
        }
    }
</script>