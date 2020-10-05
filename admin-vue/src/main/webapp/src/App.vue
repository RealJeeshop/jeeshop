<template>
  <div id="app">
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
    <div class="page-content">
      <div class="side-nav">
        <SideNavItem icon="fas fa-home" text="Overview" to="/" />
        <SideNavItem v-if="loggedIn" icon="fas fa-home" text="Catalogs" to="/catalogs" />
        <SideNavItem v-if="loggedIn" icon="fas fa-home" text="Orders" to="/orders" />
        <SideNavItem v-if="loggedIn" icon="fas fa-home" text="Stats" to="/stats" />
        <SideNavItem v-if="loggedIn" icon="fas fa-home" text="Users" to="/users" />
        <SideNavItem v-if="loggedIn" icon="fas fa-home" text="Emails, Newsletters" to="/emails" />
      </div>
      <div class="content">
        <router-view></router-view>
      </div>
    </div>
  </div>
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
