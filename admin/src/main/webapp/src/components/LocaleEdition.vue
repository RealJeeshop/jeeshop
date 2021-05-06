<template>
  <v-dialog v-model="open" persistent max-width="900">
    <v-card>
      <v-card-title v-if="presentation.locale">Edit presentation for locale {{ presentation.locale }}</v-card-title>
      <v-card-title v-else class="headline">Add new presentation</v-card-title>
      <v-card-text>
        <div v-if="!presentation.locale" class="fields-container">
          <Select label="Select a locale" name="locale" :items="availableLocales" @on-update="update"/>
        </div>
        <div class="fields-container">
          <Input label="Name" name="displayName" :value="presentation.displayName" @on-update="update"/>
          <Input label="Promotion text" name="promotion" :value="presentation.promotion" @on-update="update"/>
          <Textarea label="Short description" name="shortDescription" :value="presentation.shortDescription"
                    @on-update="update"/>
          <Textarea label="Medium description" name="mediumDescription" :value="presentation.mediumDescription"
                    @on-update="update"/>
          <Textarea label="Long description" name="longDescription" :value="presentation.longDescription"
                    @on-update="update"/>
        </div>
        <div class="fields-container">

          <FileInput label="Thumbnail" placeholder="Add a thumbnail" name="thumbnail" :value="presentation.thumbnail"
                     :rules="rules" @on-file-selected="uploadImage('thumbnail', $event)"/>

          <FileInput label="Small Image" placeholder="Add small image" name="smallImage"
                     :value="presentation.smallImage"
                     @on-file-selected="uploadImage('smallImage', $event)"/>

          <FileInput label="Large Image" placeholder="Add large image" name="largeImage"
                     :value="presentation.largeImage"
                     @on-file-selected="uploadImage('largeImage', $event)"/>
          <!--          <Input label="Video" name="video" :value="presentation.video" @on-update="update"/>-->
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn v-if="presentation" color="black darken-1" text @click="addLocale()">Add</v-btn>
        <v-btn v-else color="black darken-1" text @click="updateLocale()">Update</v-btn>
        <v-btn color="red darken-1" text @click="cancel()">Cancel</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import Input from "./inputs/Input";
import Select from "./inputs/Select";
import Textarea from "./inputs/Textarea";
import FileInput from "./inputs/FileInput";
import {MediasAPI} from "@/api";

export default {
  name: 'LocaleEdition',
  components: {FileInput, Textarea, Select, Input},
  props: {
    open: Boolean,
    data: Object,
    itemType: String,
    itemId: Number
  },
  data() {
    return {
      images: [],
      thumbnail: null,
      presentation: this.data ? this.data : {},
      availableLocales: [{value: "en", text: "English"}, {text: "French", value: "fr"}, {text: "Chinese", value: "zh"}],
      rules: [
        value => !value || value.size < 2000000 || 'Avatar size should be less than 2 MB!',
      ],
    }
  },
  watch: {
    data() {
      this.presentation = this.data ? this.data : {}
    }
  },
  methods: {
    cancel() {
      this.$emit("on-cancel")
    },
    addLocale() {
      MediasAPI.uploadImages(this.itemType, this.itemId, this.presentation.locale, this.images)
          .then(() => {
            this.$store.dispatch("catalogs/uploadCatalogMedia", {
              itemType: this.itemType,
              itemId: this.itemId,
              locale: this.presentation.locale,
              files: this.images
            })
          }).then(() => {
        this.$emit("on-save", this.presentation)
        console.log("upload suceeded")
      }).catch((e) => console.log("error upload image : " + JSON.stringify(e)))
    },
    updateLocale() {
      // TODO dispatch the update of the presentation
      this.$emit("on-save", this.presentation)
    },
    update(field) {
      this.presentation[field.key] = field.value
    },
    uploadImage(name, file) {

      this.images.push({
        field: name,
        file: file
      })
    }
  }
}
</script>

<style lang="scss" scoped>

</style>