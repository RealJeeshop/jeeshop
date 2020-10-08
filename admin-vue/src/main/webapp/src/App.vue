<template>
    <v-app id="app">
        <header>
            <div class="header-left">
                <div class="app-name"><em>Jeeshop Admin</em></div>
                <div class="app-nav">
                    <router-link to="/settings">Settings</router-link>
                    <router-link to="/help">Help</router-link>
                </div>
            </div>
            <div class="header-right">
                <Login />
            </div>

        </header>
        <v-main>
            <div class="page-content">
                <v-navigation-drawer permanent expand-on-hover>
                    <SideNavItem icon="fas fa-home" text="Overview" to="/" />
                    <SideNavItem v-if="loggedIn" icon="fas fa-home" text="Catalogs" to="/catalogs" />
                    <SideNavItem v-if="loggedIn" icon="fas fa-home" text="Orders" to="/orders" />
                    <SideNavItem v-if="loggedIn" icon="fas fa-home" text="Stats" to="/stats" />
                    <SideNavItem v-if="loggedIn" icon="fas fa-home" text="Users" to="/users" />
                    <SideNavItem v-if="loggedIn" icon="fas fa-home" text="Emails, Newsletters" to="/emails" />
                </v-navigation-drawer>
                <div class="content">
                    <router-view :key="$route.fullPath"></router-view>
                </div>
            </div>
        </v-main>
    </v-app>
</template>

<script>

    import SideNavItem from "./components/SideNavItem";
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
    }
</script>

<style scoped>

    .header-left {
        display: flex;
    }

    .header-right {
        display: flex;
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
