<template>
  <div class="item-edit-container">
    <v-form class="form-container" ref="form">
      <v-flex flex-column>
        <div>
          <strong>Langue du magasin :</strong> fr
        </div>
        Horaires du magasin
        Adresse du magasin
      </v-flex>
    </v-form>

  </div>

</template>

<script>
export default {
}
</script>

<style lang="scss" scoped>
.item-edit-container {
  margin: auto;
  height: 100%;
}
.form-container {
  margin: 0;
  height: 100%;
}
</style>