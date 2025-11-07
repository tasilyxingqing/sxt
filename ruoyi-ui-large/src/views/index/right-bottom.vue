<script setup lang="ts">
import {nextTick, onBeforeUnmount, ref} from 'vue';
import * as echarts from 'echarts';
import moment from 'moment'
import {defineExpose} from "vue/dist/vue";

defineExpose({setData})

const myChart = ref({})

function setData(memList) {
  let timeList = memList?.map(i => moment(i.time).format("HH:mm:ss"))
  let dataList = memList?.map(i => (i.data*100).toFixed(2))
  nextTick(() => {
    const chartDom = document.getElementById('rightBottomChart');
    let chart = echarts.init(chartDom);
    myChart.value = chart
    let option;
    option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['Rainfall']
      },
      toolbox: {
        show: true,
        feature: {
          magicType: { show: true, type: ['line', 'bar'] },
          restore: { show: true },
          saveAsImage: { show: true }
        }
      },
      calculable: true,
      xAxis: [
        {
          type: 'category',
          data: timeList
        }
      ],
      yAxis: [
        {
          type: 'value',
          min: 0,
          max: 100,
          axisLabel: {
            formatter: '{value} %'
          }
        }
      ],
      series: [
        {
          type: 'bar',
          data: dataList,
          markPoint: {
            data: [
              { type: 'max', name: 'Max' },
              { type: 'min', name: 'Min' }
            ]
          },
          markLine: {
            data: [{ type: 'average', name: 'Avg' }]
          }
        },
      ]
    };
    window.addEventListener("resize", resize);

    chart.setOption(option);
  })
}

function resize() {
  myChart.value.resize();
}

onBeforeUnmount(() => {
  window.removeEventListener("resize",resize);
})
</script>

<template>
  <div class="right_bottom_wrap beautify-scroll-def">
    <div style="width: 100%;height: 300px;" id="rightBottomChart"></div>
  </div>
</template>

<style scoped lang="scss">
.right_bottom {
  width: 100%;
  height: 100%;
}
</style>
