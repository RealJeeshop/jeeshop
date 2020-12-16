<template>
    <div class="page-content column padded-xl">
        <div v-if="loggedIn">
            <div></div>
        </div>
        <div v-else class="flex full column x-centered">
            <Login />
        </div>
    </div>
</template>

<script>
    import Login from './Login'
    import {mapState} from "vuex";
    export default {
        name: 'HomePage',
        components: {Login},
        computed: mapState({
            error: state => state.session.error,
            loading: state => state.session.loading,
            loggedIn: state => state.session.loggedIn
        }),
    }
</script>

<style>
    .login-container {

    }
</style>