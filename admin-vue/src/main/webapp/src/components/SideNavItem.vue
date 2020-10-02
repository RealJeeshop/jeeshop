<template>
    <router-link class="side-nav-item" :to="to">
        <i :class="icon" />
        <span>{{text}}</span>
    </router-link>
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

<style scoped>
    .side-nav-item {
        display: flex;
        align-items: center;
        padding-right: 1em;
        padding-left: 1em;
        height: 3em;
        text-decoration: none;
        color: #337AB7;
    }

    .side-nav-item:hover {
        background-color: #337AB7;
        color: white;
    }

    .side-nav-item span {
        margin-left: 1.5em;
    }

</style>