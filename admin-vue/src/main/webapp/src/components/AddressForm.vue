<template>
    <div class="address-block">
        <h4>{{label}}</h4>
        <Select placeholder="Gender" :items="genders" :value="address.gender" dense/>
        <Input placeholder="First name" :value="address.firstname" dense/>
        <Input placeholder="Last name" :value="address.lastname" dense/>
        <Input placeholder="Company" :value="address.company" dense/>
        <Input placeholder="Number and street" :value="address.street" dense/>
        <Input placeholder="City" :value="address.city" dense/>
        <Input placeholder="Zip code" :value="address.zipCode" dense/>
        <Input placeholder="Country code" :value="address.countryIso3Code" dense />
    </div>

</template>

<script>

    import Input from "./inputs/Input";
    import Select from "./inputs/Select";
    export default {
        name: "AddressForm",
        props: {
            label: String,
          value: Object
        },
        data() {
          return {
              address: this.value ? this.value : {},
              genders: [{value: "Mm.", text: "Ms"}, {value: "M.", text: "Mr"}],
          }
        },
        watch: {
          value() {
              this.address = this.value ? this.value : {}
          }
        },
        components: {Select, Input}
    }
</script>

<style lang="scss" scoped>
    .address-block {
        flex: 1;
        width: 100%;
    }
</style>