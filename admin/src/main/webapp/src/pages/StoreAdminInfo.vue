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
            <SchedulesEditor :value="schedules" />
          </div>
        </v-flex>

      </div>
    </v-form>
  </div>
</template>

<script>
import {mapState} from "vuex";
import Input from "@/components/inputs/Input";
import PresentationTable from "@/components/PresentationsTable";
import DateField from "@/components/inputs/DateField";
import _ from 'lodash'
import SchedulesEditor from "@/components/SchedulesEditor";

export default {
  components: {SchedulesEditor, DateField, Input, PresentationTable, },
  data() {
    return {
      presentations: {},
      addressModification: false,
      required: [
        value => !!value || this.$t("common.required"),
      ],
    }
  },
  computed: {
    ...mapState({
      store(state) {
        console.log('state.sessions.user : ' + JSON.stringify(state.session.user))
        let login = state.session.user
            ? state.session.user.login
            : null

        let store =  state.catalogs.stores.filter(s => {
          console.log('s : ' + JSON.stringify(s.owner))
          return s.owner === login
        })[0] // FIXME

        console.log('store : ' + JSON.stringify(store))
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
      let groupedBy = _.groupBy(schedules, s => s.dayOfWeek)
      console.log('groupedBy : ' + JSON.stringify(groupedBy))
      return groupedBy
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
    onSelectLocale() {

    },
    fetchStore() {
      this.$store.dispatch("catalogs/getManagedItems", "stores")
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