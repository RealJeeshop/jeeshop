<template>
  <v-flex justify-space-around>
    <div v-for="day in dayOfWeek" :key="`day-${day.value}`" class="schedule-day-container">
      <div>{{day.text}}</div>
      <div class="schedule-time-container">
        <div v-for="(schedule, i) in schedules[day.value] ? schedules[day.value] : []" :key="`schedule-${day.value}-${i}`" class="schedule-time-item">
          <Input name="timeOpen" :value="schedule.timeOpen" label="Ouvre à" placeholder="Ouvre à" @on-update="update"/>
          <Input name="timeClose" :value="schedule.timeClose" label="Ferme à" placeholder="Ferme à" @on-update="update" />
        </div>
        <i @click="addScheduleFor(day.value)" class="fas fa-plus clickable add-button" />
      </div>
    </div>
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
      schedules: this.value,
      modify: false,
      dayOfWeek: [{value: 1, text: "Lundi"}, {value: 2, text: "Mardi"},
        {value: 3, text: "Mercredi"}, {value: 4, text: "Jeudi"},
        {value: 5, text: "Vendredi"}, {value: 6, text: "Samedi"},
        {value: 7, text: "Dimanche"}]
    }
  },
  methods: {
    update(a, b) {
      console.log('a : ' + JSON.stringify(a))
      console.log('b : ' + JSON.stringify(b))
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

  .schedule-time-container {


    .schedule-time-item {

      display: flex;
    }
  }

  .schedule-time-container.i {
      margin: 0.4em;
      text-align: center;
    width: 100%;
  }

  .add-button {
    text-align: center;
    width: 100%;
  }


}

</style>