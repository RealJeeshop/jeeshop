<template>
    <div class="page-content column padded-xl">
        <div v-if="loggedIn">
            <AdminDashboard v-if="$store.getters['session/isUserInRole']('admin')"/>
            <StoreAdminDashboard v-if="$store.getters['session/isUserInRole']('store_admin')"/>
        </div>
        <div v-else class="flex full column x-centered">
            <Login @on-logged="hey"/>
        </div>
    </div>
</template>

<script>
    import Login from './Login'
    import {mapState} from "vuex";
    import AdminDashboard from "@/components/AdminDashboard";
    import StoreAdminDashboard from "@/components/StoreAdminDashboard";
    export default {
        name: 'HomePage',
        components: {StoreAdminDashboard, AdminDashboard, Login},
        computed: mapState({
            error: state => state.session.error,
            loading: state => state.session.loading,
            loggedIn: state => state.session.loggedIn
        }),
      methods: {
          hey() {
            console.log("Hey")
          }
      }
    }
</script>

<style>
    .login-container {

    }
</style>