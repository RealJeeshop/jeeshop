<template>
  <v-flex justify-space-around flex-wrap>
    <div v-for="day in dayOfWeek" :key="`day-${day.value}`" class="schedule-day-container">
      <div>{{ day.text }}</div>
      <div class="schedule-time-container">
        <div v-for="(schedule, i) in schedules[day.value] ? schedules[day.value] : []"
             :key="`schedule-${day.value}-${i}`" class="schedule-time-item">
          <Input type="time" name="timeOpen" :value="{id: day.value, value: schedule.timeOpen}" label="Ouvre à"
                 placeholder="Ouvre à" min="00:00" max="23:59" @on-update="update"/>
          <Input type="time" name="timeClose" :value="{id: day.value, value: schedule.timeOpen}" label="Ferme à"
                 placeholder="Ferme à" min="00:00" max="23:59" @on-update="update"/>
        </div>
        <a class="add-button" @click="addScheduleFor(day.value)">Ajouter une horaire</a>
      </div>
    </div>
    <v-btn color="primary" @click="save">Save</v-btn>
  </v-flex>
</template>

<script>

import Input from "@/components/inputs/Input";

export default {
  props: {
    value: Object
  },
  components: {Input},
  data() {
    return {
      menu2: false,
      schedules: this.value,
      modify: false,
      dayOfWeek: [{value: 1, text: "Lundi"}, {value: 2, text: "Mardi"},
        {value: 3, text: "Mercredi"}, {value: 4, text: "Jeudi"},
        {value: 5, text: "Vendredi"}, {value: 6, text: "Samedi"},
        {value: 7, text: "Dimanche"}]
    }
  },
  methods: {
    update(a) {
      this.schedules[a.id][a.key] = a.value
    },
    addScheduleFor(dayOfWeek) {
      let schedules = this.schedules ? this.schedules : {}

      let exitingsSchedules = schedules[dayOfWeek]

      if (exitingsSchedules) {
        schedules[dayOfWeek].push({timeOpen: '09:00', timeClose: '19:00'})

      } else {
        schedules[dayOfWeek] = [{timeOpen: '09:00', timeClose: '19:00'}]
      }

      this.schedules = Object.assign({}, schedules)

    },
    save() {
      console.log('this.schedules : ' + JSON.stringify(this.schedules))
    }
  }
}
</script>

<style lang="scss" scoped>
.schedule-day-container {

  padding: 0.5em;
  margin: 0.1em;
  background-color: #F2F2F2;
  border: 1px solid #e0e0e0;
  flex: 1;
  display: flex;
  flex-direction: column;


  .schedule-time-container {


    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    flex: 2;

    .schedule-time-item {

      display: flex;
    }
  }

  .add-button {
    display: block;
    text-align: center;
    width: 100%;
  }


}

</style>