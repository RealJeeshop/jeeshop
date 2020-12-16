<template>
    <div class="login-container">
        <h1>{{ $t("login.title") }}</h1>
        <div class="login-form-container">
            <v-text-field v-model="email" placeholder="email" :hide-details="true" />
            <v-text-field v-model="password" placeholder="password" type="password" :hide-details="true" />
            <v-btn color="primary" @click="login({email, password})">Login</v-btn>
            <!--<p v-if="error" class="error">Bad login information</p>-->
        </div>
    </div>

</template>

<script>
    import {mapActions} from "vuex";

    export default {
        name: 'Login',
        data() {
            return {
                email: "",
                password: ""
            }
        },

        methods: {
            ...mapActions('session', ['login']),
        }
    }
</script>

<style lang="scss">

    .login-container {

        h1 {
            text-align: center;
            margin-bottom: 1em;
        }

        padding: 40px 70px 55px;
        max-width: 640px;
        min-width: 450px;
        background-color: white;

        .login-form-container {
            display: flex;
            flex-direction: column;

            .v-input {
                margin-bottom: 1em;
            }
        }
    }


</style>