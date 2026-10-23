<template>
  <div class="field-container image-upload">
    <div>{{ label }}</div>
    <div class="image-preview">
      <v-img v-if="imageUrl" :src="imageUrl" height="150"></v-img>
      <v-icon v-else>mdi-image</v-icon>
    </div>
    <div class="image-edit">
      <v-file-input accept="image/*"
                    :rules="rules"
                    v-model="file"
                    :placeholder="placeholder"
                    @change="handleFileSelect">
      </v-file-input>
    </div>
  </div>
</template>

<script>
import axios from "axios"

export default {
  name: 'FileInput',
  props: {
    label: String,
    value: Object,
    placeholder: String,
    name: String,
    hint: String,
    rules: Array
  },
  data() {
    return {
      imageUrl: null,
      file: null
    }
  },
  watch: {
    value() {
      this.downloadFile()
    }
  },
  methods: {
    handleFileSelect() {
      this.imageUrl = URL.createObjectURL(this.file)
      this.$emit("on-file-selected", this.file)
    },
    _imageEncode(arrayBuffer) {
      new Uint8Array(arrayBuffer)
      let b64encoded = btoa([].reduce.call(new Uint8Array(arrayBuffer), function (p, c) {
        return p + String.fromCharCode(c)
      }, ''))
      let mimetype = "image/jpeg"
      return "data:" + mimetype + ";base64," + b64encoded
    },
    downloadFile() {
      if (this.value && this.imageUrl == null) {
        axios.get(this.value.uri, {
          responseType: "arraybuffer",
        }).then(res => {
          let filename = this.value.uri.slice(this.value.uri.lastIndexOf("/") + 1);
          this.file = new File([res.data], filename)
          this.imageUrl = this._imageEncode(res.data)
        })

      }

    }
  },
  created() {
    this.downloadFile()
  }
}
</script>

<style lang="scss" scoped>

.image-upload {


  .image-edit {

    input {
      display: none;

      + label {

      }
    }

  }

  .image-preview {

    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 150px;
  }
}

.avatar-upload {
  position: relative;
  max-width: 205px;
  margin: 50px auto;

  .avatar-edit {
    position: absolute;
    right: 12px;
    z-index: 1;
    top: 10px;

    input {
      display: none;

      + label {
        display: inline-block;
        width: 34px;
        height: 34px;
        margin-bottom: 0;
        border-radius: 100%;
        background: #FFFFFF;
        border: 1px solid transparent;
        box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.12);
        cursor: pointer;
        font-weight: normal;
        transition: all .2s ease-in-out;

        &:hover {
          background: #f1f1f1;
          border-color: #d6d6d6;
        }

        &:after {
          content: "\F3EB";
          font-family: "Material Icons";
          color: #757575;
          position: absolute;
          top: 10px;
          left: 0;
          right: 0;
          text-align: center;
          margin: auto;
        }
      }
    }
  }

  .avatar-preview {
    width: 192px;
    height: 192px;
    position: relative;
    border-radius: 100%;
    border: 6px solid #F8F8F8;
    box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.1);

    > div {
      width: 100%;
      height: 100%;
      border-radius: 100%;
      background-size: cover;
      background-repeat: no-repeat;
      background-position: center;
    }
  }
}
</style>