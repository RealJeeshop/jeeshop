<template>
    <v-app id="app">
            <v-main class="page-content">
                <v-toolbar color="#272727" dark flat app>
                    <v-app-bar-nav-icon class="d-sm-none"></v-app-bar-nav-icon>
                    <v-toolbar-title>Jeeshop Admin</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <LoggedMenu />
                    <template v-if="loggedIn" v-slot:extension>
                        <v-tabs align-with-title>
                            <v-tabs-slider color="yellow"></v-tabs-slider>
                            <SideNavItem icon="fas fa-home" text="Overview" to="/" />
                            <SideNavItem v-if="isStoreAdmin" icon="fas fa-home" text="My Store" to="/store/info" />
                            <SideNavItem icon="fas fa-book" text="Catalog" to="/catalogs" />
                            <SideNavItem icon="fas fa-shopping-cart" text="Orders" to="/orders" />
                            <SideNavItem icon="fas fa-chart-bar" text="Stats" to="/stats" />
                            <SideNavItem v-if="isAdmin" icon="fas fa-user" text="Users" to="/users" />
                            <SideNavItem icon="fas fa-envelope" text="Emails, Newsletters" to="/emails" />
                        </v-tabs>
                    </template>
                </v-toolbar>
                <router-view :key="$route.fullPath"></router-view>
                <v-footer color="#272727" dark>
                    Real Jeeshop &copy; 2020
                </v-footer>
            </v-main>

    </v-app>
</template>

<script>

    import SideNavItem from "./components/NavItem";
    import { mapState } from 'vuex'
    import LoggedMenu from "./components/LoggedMenu";
    import './App.scss'

    export default {
        name: 'App',
        components: {
            SideNavItem,
          LoggedMenu
        },
        computed: {
          ...mapState({
            loggedIn: state => state.session.loggedIn,
          }),
          isAdmin() {
            return this.$store.getters['session/isUserInRole']('admin')
          },
          isStoreAdmin() {
            return this.$store.getters['session/isUserInRole']('store_admin')
          }
        },
        created() {
          console.log("creatin app")
          if (this.$store.getters["session/isLoggedIn"]) {
            console.log("retrieving logged data")
            this.$store.dispatch("session/getUserInfo")
            this.$store.dispatch("catalogs/init")
          }
        }
    }
</script>

<style scoped>

    .header-left {
        display: flex;
        align-items: center;
        height: 100%;
    }

    .header-right {
        display: flex;
        align-items: center;
        height: 100%;
    }

    footer {
        color: white;
        display: flex;
        justify-content: center;
        padding: 3em 4em 3em 4em;
    }
</style>
