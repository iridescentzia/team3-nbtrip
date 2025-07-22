<script setup>
import { ref, computed } from 'vue';
import { FileChartColumn, Trash2 } from 'lucide-vue-next';

// trip_name, startDate, endDate 받아오기
const props = defineProps({
  tripName: String,
  startDate: String,
  endDate: String,
});

// 날짜 출력 포맷 함수(YYYY-MM-DD -> YYYY.MM.DD)
const formattedDate = computed(() => {
  const start = props.startDate?.replaceAll('-', '.') ?? '';
  const end = props.endDate?.replaceAll('-', '.') ?? '';
  return start && end ? `${start} ~ ${end}` : '';
});

// 탭 목록
const tabs = ['그룹 지출 내역', '선결제 내역', '그룹 관리'];
const activeTab = ref(tabs[0]); // 현재 활성 탭 상태(기본 값 : 첫 번째(그룹 지출 내역))

// 탭 선택 함수
function selectTab(tab) {
  activeTab.value = tab;
}
</script>

<template>
  <div
    class="w-[342px] rounded-2xl p-4 flex flex-col justify-between font-['IBM_Plex_Sans_KR'] shadow-md bg-gradient-to-b from-[#A2D2FF80] via-[#B9DDFF80] to-[#FFFFFF80]"
  >
    <!-- 카드 헤더 -->
    <div class="flex justify-between items-start">
      <!-- 왼쪽: 여행 제목 + 날짜 -->
      <div class="flex flex-col gap-1">
        <div class="flex items-center gap-2">
          <div class="text-black text-2xl font-bold leading-7 m-0">
            {{ props.tripName }}
          </div>
          <FileChartColumn
            class="w-5 h-5 text-gray-500 cursor-pointer transition-colors hover:text-gray-800"
          />
        </div>
        <div class="text-gray-600 text-sm font-normal opacity-80">
          {{ formattedDate }}
        </div>
      </div>

      <!-- 오른쪽: 휴지통 아이콘 -->
      <Trash2
        class="w-5 h-5 text-gray-500 cursor-pointer transition-colors hover:text-gray-800"
      />
    </div>

    <!-- 카드 구분선 -->
    <div class="w-full border-t border-[#A2D2FF99] my-2"></div>

    <!-- 탭 목록 -->
    <div class="flex justify-between items-center gap-2">
      <div
        v-for="tab in tabs"
        :key="tab"
        class="text-sm font-semibold leading-5 cursor-pointer px-2 py-1 rounded-xl text-center whitespace-nowrap flex-1 transition hover:bg-black/5"
        :class="{
          'text-[#2f6be9]': activeTab === tab,
          'text-[#575757]': activeTab !== tab,
        }"
        @click="selectTab(tab)"
      >
        {{ tab }}
      </div>
    </div>
  </div>
</template>
