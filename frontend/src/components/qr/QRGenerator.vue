<template>
  <div>
    <qrcode-vue :value="qrCodeValue" :size="200" level="H"></qrcode-vue>
  </div>
</template>

<script>
import QrcodeVue from 'qrcode.vue';

export default {
  components: {
    QrcodeVue,
  },
  data() {
    return {
      jsonData: {
        merchantID: 1,
      },
      qrCodeValue: '',
    };
  },
  created() {
    // Convert JSON object to a string
    this.qrCodeValue = JSON.stringify(this.jsonData);
  },
};
</script>
