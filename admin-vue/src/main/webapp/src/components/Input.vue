<template>
    <div class="field-container">
        <label>
            <span>{{label}}</span>
            <input type="text" v-model="value" :placeholder="placeholder">
        </label>
    </div>
</template>

<script>
    export default {
        name: 'Input',
        props: {
            label: String,
            value: String,
            placeholder: String
        }
    }
</script>