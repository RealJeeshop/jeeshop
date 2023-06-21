<template>
  <div class="item-edit-container">
    <v-form class="form-container" ref="form">
      <div class="default-fields-container column">
        <Input label="Name" name="name" :value="store.name" :rules="required" @on-update="update"/>
        <Input label="Description" name="description" placeholder="description"
               :value="store.description" @on-update="update"/>

        <div class="flex one-half">
          <DateField name="startDate" label="Start Date" placeholder="Choose visibility date"
                     :value="store.startDate" @on-update="update"/>

          <DateField name="endDate" label="End Date" placeholder="Choose visibility end date"
                     :value="store.endDate" @on-update="update"/>
        </div>
        <PresentationTable :value="presentations" @update-locale="onSelectLocale"/>

        <v-flex class="section-container" flex-column>
          <v-flex justify-space-between>
            <strong>Adresse du magasin</strong>
            <div v-if="!addressModification" class="clickable" @click="modifyAddress">
              {{addressAsString ? "Modifier" : "Ajouter"}}
            </div>
          </v-flex>
          <div v-if="!addressModification">{{addressAsString}}</div>
          <v-flex v-else flex-wrap align-center>
            <Input label="N° et nom de rue" name="street" :value="address.street" :rules="required" @on-update="updateAddress"/>
            <Input label="Code postal" name="zipCode" :value="address.zipCode" placeholder="Code postal" @on-update="updateAddress"/>
            <Input label="Ville" name="city" :value="address.city" placeholder="Ville" @on-update="updateAddress" />
            <v-btn color="primary" @click="saveAddress"><i class="fas fa-check"></i> </v-btn>
          </v-flex>
        </v-flex>

        <v-flex class="section-container" flex-column>
          <v-flex justify-space-between>
            <strong>Horaires du magasin</strong>
          </v-flex>
          <div>
            <SchedulesEditor :value="schedules"/>
          </div>
        </v-flex>

      </div>
    </v-form>

    <LocaleEdition v-if="showLocaleEdition"
                   :open="showLocaleEdition"
                   :data="store.availableLocales ? store.availableLocales[this.locale] : null"
                   @on-cancel="showLocaleEdition = false"
                   @on-save="saveLocale"/>
  </div>
</template>

<script>
import {mapState} from "vuex";
import Input from "@/components/inputs/Input";
import PresentationTable from "@/components/PresentationsTable";
import DateField from "@/components/inputs/DateField";
import _ from 'lodash'
import SchedulesEditor from "@/components/SchedulesEditor";
import LocaleEdition from "@/components/LocaleEdition";

export default {
  components: {SchedulesEditor, DateField, Input, PresentationTable, LocaleEdition},
  data() {
    return {
      presentations: {},
      showLocaleEdition: false,
      addressModification: false,
      required: [
        value => !!value || this.$t("common.required"),
      ],
    }
  },
  computed: {
    ...mapState({
      store(state) {
        let storeId = state.session.user
            ? state.session.user.storeId
            : null

        let store = state.catalogs.stores.filter(s => {
          return s !== null && s.storeId === storeId
        })[0] // FIXME

        if (store) {
          this.presentations = {
            itemType: this.itemType,
            itemId: this.itemId,
            availableLocales: store.localizedPresentation
          }
        }

        return store ? store : {}
      },
      user(state) {
        return state.session.user
      },
    }),
    address() {
      return this.premisses ? this.premisses.address : null
    },
    premisses() {
      return this.store.premisses ? this.store.premisses[0] : null
    },
    addressAsString() {
      return this.address
          ? `${this.address.street} ${this.address.zipCode} ${this.address.city}`
          : null
    },
    schedules() {

      let schedules = this.premisses ? this.premisses.schedules : []
      return _.groupBy(schedules, s => s.dayOfWeek)
    }
  },
  methods: {
    update(field) {
      this.item[field.key] = field.value
    },
    updateAddress(field) {
      this.address[field.key] = field.value
    },
    saveAddress() {
      this.addressModification = false

    },
    modifyAddress() {
      this.addressModification = true
    },
    onSelectLocale(locale) {
      this.locale = locale
      this.$store.dispatch("catalogs/getPresentation", {
        itemType: "stores",
        itemId: this.$store.getters["session/getStoreId"],
        locale: locale
      })

      this.showLocaleEdition = true
    },
    saveLocale(presentation) {
      this.showLocaleEdition = false

      let existingLocales = this.presentations.availableLocales ? this.presentations.availableLocales : []
      if (existingLocales.findIndex(l => l === presentation.locale) === -1) {
        this.$store.dispatch("catalogs/attachPresentation", {
          itemType: "stores",
          itemId: this.$store.getters["session/getStoreId"],
          locale: this.locale,
          presentation: presentation
        })

        this.presentations = {
          itemType: this.itemType,
          itemId: this.itemId,
          availableLocales: [presentation.locale].concat(existingLocales)
        }

        let newAvailableLocale = {}
        newAvailableLocale[presentation.locale] = presentation;
        this.item.availableLocales = Object.assign({}, this.item.availableLocales ? this.item.availableLocales : {}, newAvailableLocale)
      } else {

        this.$store.dispatch("catalogs/updatePresentation", {
          itemType: this.itemType,
          itemId: this.itemId,
          locale: this.locale,
          presentation: presentation
        })
      }
    },
    fetchStore() {
      let storeId = this.$store.getters["session/getStoreId"]
      console.log('storeId : ' + JSON.stringify(storeId)) // FIXME
      this.$store.dispatch("catalogs/getManagedStore", 1)
    }
  },
  created() {
    this.fetchStore()
  }
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
.section-container {
  margin-bottom: 2em;
}

.field-container {
  min-width: 0 !important;
  align-items: center;
}
</style>