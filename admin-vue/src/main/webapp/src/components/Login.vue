<template>
    <div v-if="loggedIn" class="welcome">
        <span>Welcome to Jeeshop !</span>
        <v-menu left bottom>
            <template v-slot:activator="{ on, attrs }">
                <v-btn icon v-bind="attrs" v-on="on">
                    <v-icon>mdi-dots-vertical</v-icon>
                </v-btn>
            </template>

            <v-list>
                <v-list-item @click="this.$router.push('/settings')">
                    <v-list-item-title>Settings</v-list-item-title>
                </v-list-item>
                <v-list-item @click="this.$router.push('/help')">
                    <v-list-item-title>Help</v-list-item-title>
                </v-list-item>
                <v-list-item @click="logOut()">
                    <v-list-item-title>Log out</v-list-item-title>
                </v-list-item>
            </v-list>
        </v-menu>
    </div>

    <div v-else class="login-form-container">
        <v-text-field v-model="email" placeholder="email" :hide-details="true" solo />
        <v-text-field v-model="password" placeholder="password" type="password" :hide-details="true" solo />
        <v-btn color="primary" @click="login({email: email, password: password})">Login</v-btn>
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
        methods: {
            ...mapActions('session', ['login']),
            logOut() {
                this.$store.dispatch('session/logOut')
                this.$router.replace('/')
            }
        },
    }
</script>

<style lang="scss">
    .login-form-container {
        .v-text-field.v-text-field--solo .v-input__control {
            min-height: 36px;
        }
    }
</style>

<style lang="scss" scoped>

    .welcome {
        display: flex;
        align-items: center;

        span {
            padding-right: 1em;
        }
    }

    .login-form-container {
        display: flex;
        align-items: center;


        .v-input {
            margin-right: 1em;
        }

        label {
            padding-right: 1em;
        }
    }

</style>