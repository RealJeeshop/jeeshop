<template>
  <div class="page-content column">
    <v-toolbar color="#484848" dark flat>
      <v-tabs class="sub-header-menu" align-with-title>
        <v-tabs-slider color="yellow"></v-tabs-slider>
        <v-tab to="/store/info">Info</v-tab>
        <v-tab to="/store/settings">Settings</v-tab>
      </v-tabs>
    </v-toolbar>

    <router-view />
  </div>
</template>

<script>
export default {
}
</script>

<style>

</style>