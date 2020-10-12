<template>
    <v-tab :to="to">{{text}}</v-tab>
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