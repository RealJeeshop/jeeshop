<template>
    <v-app id="app">
        <v-content>
            <v-toolbar color="#272727" dark flat app>
                <v-app-bar-nav-icon class="d-sm-none"></v-app-bar-nav-icon>
                <v-toolbar-title>Jeeshop Admin</v-toolbar-title>
                <v-spacer></v-spacer>
                <Login />
                <template v-if="loggedIn" v-slot:extension>
                    <v-tabs  align-with-title>
                        <v-tabs-slider color="yellow"></v-tabs-slider>
                        <SideNavItem icon="fas fa-home" text="Overview" to="/" />
                        <SideNavItem icon="fas fa-book" text="Catalog" to="/catalogs" />
                        <SideNavItem icon="fas fa-shopping-cart" text="Orders" to="/orders" />
                        <SideNavItem icon="fas fa-chart-bar" text="Stats" to="/stats" />
                        <SideNavItem icon="fas fa-user" text="Users" to="/users" />
                        <SideNavItem icon="fas fa-envelope" text="Emails, Newsletters" to="/emails" />
                    </v-tabs>
                </template>
            </v-toolbar>
            <v-main class="page-content">
                <router-view :key="$route.fullPath"></router-view>
            </v-main>
            <v-footer color="#272727" dark>
                Real Jeeshop &copy; 2020
            </v-footer>
        </v-content>
    </v-app>
</template>

<script>

    import SideNavItem from "./components/NavItem";
    import { mapState } from 'vuex'
    import Login from "./components/Login";
    import './App.scss'

    export default {


        name: 'App',
        components: {
            SideNavItem,
            Login
        },
        computed: mapState({
            loggedIn: state => state.session.loggedIn
        }),
        created() {
            this.$store.dispatch('catalogs/init')
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


<!--
<template>
  <v-app>
    <v-app-bar
      app
      color="primary"
      dark
    >
      <div class="d-flex align-center">
        <v-img
          alt="Vuetify Logo"
          class="shrink mr-2"
          contain
          src="https://cdn.vuetifyjs.com/images/logos/vuetify-logo-dark.png"
          transition="scale-transition"
          width="40"
        />

        <v-img
          alt="Vuetify Name"
          class="shrink mt-1 hidden-sm-and-down"
          contain
          min-width="100"
          src="https://cdn.vuetifyjs.com/images/logos/vuetify-name-dark.png"
          width="100"
        />
      </div>

      <v-spacer></v-spacer>

      <v-btn
        href="https://github.com/vuetifyjs/vuetify/releases/latest"
        target="_blank"
        text
      >
        <span class="mr-2">Latest Release</span>
        <v-icon>mdi-open-in-new</v-icon>
      </v-btn>
    </v-app-bar>

    <v-main>
      <HelloWorld/>
    </v-main>
  </v-app>
</template>

<script>
import HelloWorld from './components/HelloWorld';

export default {
  name: 'App',

  components: {
    HelloWorld,
  },

  data: () => ({
    //
  }),
};
</script>
-->
