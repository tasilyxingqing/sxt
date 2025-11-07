<script setup lang="ts">
import { reactive, ref } from "vue";
import { countDeviceNum } from "@/api";
import CountUp from "@/components/count-up";
import {ElMessage} from "element-plus"

const duration = ref(2);
const state = reactive({
  totalIsupNum: 0,
  totalRtspNum: 0,
  totalOnvifNum: 0,
  totalGbNum: 0,
});


const getData = () => {
  countDeviceNum().then((res) => {
    console.log("左上--设备总览",res);
      state.totalIsupNum = res.data.totalIsupNum;
      state.totalRtspNum = res.data.totalRtspNum;
      state.totalOnvifNum = res.data.totalOnvifNum;
      state.totalGbNum = res.data.totalGbNum;
  }).catch(err=>{
    ElMessage.error(err)
  });
};
getData();
</script>

<template>
  <ul class="user_Overview flex">
    <li class="user_Overview-item" style="color: #00fdfa">
      <div class="user_Overview_nums allnum">
        <CountUp :endVal="state.totalGbNum" :duration="duration" />
      </div>
      <p>国标设备数</p>
    </li>
    <li class="user_Overview-item" style="color: #07f7a8">
      <div class="user_Overview_nums online">
        <CountUp :endVal="state.totalOnvifNum" :duration="duration" />
      </div>
      <p>onvif设备数</p>
    </li>
    <li class="user_Overview-item" style="color: #e3b337">
      <div class="user_Overview_nums offline">
        <CountUp :endVal="state.totalRtspNum" :duration="duration" />
      </div>
      <p>rtsp设备数</p>
    </li>
    <li class="user_Overview-item" style="color: #f5023d">
      <div class="user_Overview_nums laramnum">
        <CountUp :endVal="state.totalIsupNum" :duration="duration" />
      </div>
      <p>iusp设备数</p>
    </li>
  </ul>
</template>

<style scoped lang="scss">
.left-top {
  width: 100%;
  height: 100%;
}

.user_Overview {
  li {
    flex: 1;

    p {
      text-align: center;
      height: 16px;
      font-size: 16px;
    }

    .user_Overview_nums {
      width: 100px;
      height: 100px;
      text-align: center;
      line-height: 100px;
      font-size: 22px;
      margin: 50px auto 30px;
      background-size: cover;
      background-position: center center;
      position: relative;

      &::before {
        content: "";
        position: absolute;
        width: 100%;
        height: 100%;
        top: 0;
        left: 0;
      }

      &.bgdonghua::before {
        animation: rotating 14s linear infinite;
      }
    }

    .allnum {
      &::before {
        background-image: url("@/assets/img/left_top_lan.png");
      }
    }

    .online {
      &::before {
        background-image: url("@/assets/img/left_top_lv.png");
      }
    }

    .offline {
      &::before {
        background-image: url("@/assets/img/left_top_huang.png");
      }
    }

    .laramnum {
      &::before {
        background-image: url("@/assets/img/left_top_hong.png");
      }
    }
  }
}
</style>
