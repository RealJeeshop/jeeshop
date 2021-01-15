<template>
    <div class="login-container">
        <h1>{{ $t("login.title") }}</h1>
        <div class="error-block">{{error}}</div>
        <div class="login-form-container">
            <v-text-field v-model="email" placeholder="email" :hide-details="true" />
            <v-text-field v-model="password" placeholder="password" type="password" :hide-details="true" />
            <v-btn color="primary" @click="login">Login</v-btn>
            <!--<p v-if="error" class="error">Bad login information</p>-->
        </div>
    </div>

</template>

<script>

    import {mapState} from "vuex";

    export default {
        name: 'Login',
        data() {
            return {
                email: "",
                password: ""
            }
        },
      computed: mapState({
        error: state => state.session.error,
        loading: state => state.session.loading,
        loggedIn: state => state.session.loggedIn
      }),
      watch: {
        loggedIn(logged) {
          if (logged) {
            this.$emit("on-logged")
          }
        }
      },
        methods: {
            login() {
              this.$store.dispatch("session/login", {
                email: this.email,
                password: this.password
              })
            }
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