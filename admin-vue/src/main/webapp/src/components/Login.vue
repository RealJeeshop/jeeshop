<template>
    <div v-if="loggedIn" class="welcome">
        <span>Welcome to Jeeshop !</span>
    </div>
    <div v-else class="form-container">
        <label><input v-model="email" placeholder="email"></label>
        <label><input v-model="password" placeholder="password" type="password"></label>
        <button class="btn" @click="login({email: email, password: password})">login</button>
        <!--<p v-if="error" class="error">Bad login information</p>-->
    </div>
</template>

<script>
    import { mapState, mapActions } from 'vuex'

    export default {
        data() { return {
            email: null,
            password: null
        }},
        computed: mapState({
            error: state => state.session.error,
            loading: state => state.session.loading,
            loggedIn: state => state.session.loggedIn
        }),
        methods: mapActions('session', [
            'login'
        ]),
    }
</script>

<style lang="scss" scoped>

    .form-container {
        display: flex;
        label {
            padding-right: 1em;
        }
    }

</style>