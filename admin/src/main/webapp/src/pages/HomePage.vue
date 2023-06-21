<template>
  <div class="page-content column padded-xl">
    <AdminDashboard v-if="$store.getters['session/isUserInRole']('admin')"/>
    <StoreAdminDashboard v-if="$store.getters['session/isUserInRole']('store_admin')"/>
  </div>
</template>

<script>
import AdminDashboard from "@/components/AdminDashboard";
import StoreAdminDashboard from "@/components/StoreAdminDashboard";

export default {
  name: 'HomePage',
  components: {StoreAdminDashboard, AdminDashboard}
}
</script>

<style>
</style>