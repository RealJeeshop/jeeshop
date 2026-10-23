<template>
  <div class="page-content column">
    <div class="item-edit-container">
      <div class="header">
        <div>
          <h2>Associer une présentation</h2>
          <span>Les présentations sont les éléments qui s'afficheront sur la boutique en ligne. Cette étape est obligatoire</span>
        </div>
        <span @click="close" class="close-icon"></span>
      </div>

      <div class="form-container">
        <h3>Nom du produits et textes descriptifs</h3>
        <div class="fields-container">
          <Input label="Name" :value="presentation.displayName" @on-update="update" />
          <Input label="Promotion text" :value="presentation.promotion" @on-update="update"/>
          <Textarea label="Short description" :value="presentation.shortDescription" @on-update="update" />
          <Textarea label="Medium description" :value="presentation.mediumDescription" @on-update="update" />
          <Textarea label="Long description" :value="presentation.longDescription" @on-update="update"/>
        </div>
        <h3>Associer du contenu multimédia</h3>
        <div class="fields-container">

          <FileInput label="Thumbnail" :value="presentation.thumbnail" @on-update="update" />
          <FileInput label="Small Image" :value="presentation.smallImage" @on-update="update" />
          <FileInput label="Large Image" :value="presentation.largeImage" @on-update="update" />
          <FileInput label="Video" :value="presentation.video" @on-update="update" />
        </div>

        <v-btn color="primary" elevation="2" @click="saveItem()">Attacher une catégorie</v-btn>
      </div>
    </div>
  </div>

</template>

<script>
    import Input from "../components/inputs/Input";
    import FileInput from "../components/inputs/FileInput";
    import Textarea from "../components/inputs/Textarea";
    import moment from 'moment'

    export default {
        name: 'AddPresentation',
        components: {Input, FileInput, Textarea},
        data() {
            return {
                name: "",
                description: "",
                startDate: moment(),
                endDate: null,
                isLimitedInTime: false,
                presentation: {}
            }
        },
        methods: {
            update() {

            },
            close() {

            },
            saveItem() {
                this.$router.push("/products/create/link")
            }
        }
    }
</script>

<style lang="scss" scoped>
    @import "edit-form";
</style>