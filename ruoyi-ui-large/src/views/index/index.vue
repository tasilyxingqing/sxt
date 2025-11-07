<script setup lang="ts">
import ItemWrap from "@/components/item-wrap";
import LeftTop from "./left-top.vue";
import LeftCenter from "./left-center.vue";
import LeftBottom from "./left-bottom.vue";
import RightTop from "./right-top.vue";
import RightCenter from "./right-center.vue";
import RightBottom from "./right-bottom.vue";
import CenterBottom from "./center-bottom.vue";
import CenterMap from "./center-map.vue";

import {useRoute} from 'vue-router'
import {setLocalStorage} from "@/utils";
import {StorageEnum} from "@/enums";
import {nextTick, onMounted, ref} from "vue";
import {serverInfo} from "@/api";
import {ElMessage} from "element-plus";

const timer = ref({})
const RightTopRef = ref(null)
const RightCenterRef = ref(null)
const RightBottomRef = ref(null)
const CenterBottomRef = ref(null)

const route = useRoute();
setLocalStorage(StorageEnum.GB_TOKEN_STORE, route.query.token)

onMounted(()=>{
  nextTick(()=>{
    serverInfoFun()
    timer.value = setInterval(()=>{
      serverInfoFun()
    }, 2000)
  })
})

function serverInfoFun(){
  serverInfo()
      .then((res) => {
        RightTopRef.value.setData(res.data.cpu)
        RightCenterRef.value.setData(res.data.net)
        RightBottomRef.value.setData(res.data.mem)
        CenterBottomRef.value.setData(res.data.disk)
      })
      .catch((err) => {
        ElMessage.error(err);
      });
}
</script>

<template>
  <div class="index-box">
    <div class="contetn_left">
      <!-- <div class="pagetab">
        <div class="item">实时监测</div>
        <div class="item">统计分析</div>
      </div> -->
      <ItemWrap class="contetn_left-top contetn_lr-item" title="设备总览">
        <LeftTop />
      </ItemWrap>
      <ItemWrap class="contetn_left-center contetn_lr-item" title="国标统计">
        <LeftCenter />
      </ItemWrap>
      <ItemWrap
        class="contetn_left-bottom contetn_lr-item"
        title="国标设备提醒"
        style="padding: 0 10px 16px 10px"
      >
        <LeftBottom />
      </ItemWrap>
    </div>
    <div class="contetn_center">
      <CenterMap class="contetn_center_top" title="设备分布图" />
      <ItemWrap class="contetn_center-bottom" title="磁盘">
        <CenterBottom ref="CenterBottomRef"/>
      </ItemWrap>
    </div>
    <div class="contetn_right">
      <ItemWrap class="contetn_left-bottom contetn_lr-item" title="CPU">
        <RightTop ref="RightTopRef"/>
      </ItemWrap>
      <ItemWrap
        class="contetn_left-bottom contetn_lr-item"
        title="网络"
        style="padding: 0 10px 16px 10px"
      >
        <RightCenter ref="RightCenterRef"/>
      </ItemWrap>
      <ItemWrap class="contetn_left-bottom contetn_lr-item" title="内存 ">
        <RightBottom ref="RightBottomRef"/>
      </ItemWrap>
    </div>
  </div>
</template>

<style scoped lang="scss">
.index-box {
  width: 100%;
  display: flex;
  min-height: calc(100% - 64px);
  justify-content: space-between;
}
//左边 右边 结构一样
.contetn_left,
.contetn_right {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  position: relative;
  width: 540px;
  box-sizing: border-box;
  flex-shrink: 0;
}
.contetn_center {
  flex: 1;
  margin: 0 54px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  .contetn_center-bottom {
    height: 315px;
  }
}

.contetn_lr-item {
  height: 310px;
}
</style>
