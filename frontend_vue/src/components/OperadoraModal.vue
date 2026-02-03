<template>
  <div v-if="aberto" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
    <div class="bg-white rounded-xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
      <div class="p-6 border-b flex justify-between items-center bg-gray-50 rounded-t-xl">
        <h3 class="text-xl font-bold text-gray-800">{{ operadora?.razao_social }}</h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-red-500 text-3xl font-bold">&times;</button>
      </div>

      <div class="p-6">
        <div class="grid grid-cols-2 gap-4 mb-6 text-sm bg-blue-50 p-4 rounded-lg">
          <div><strong class="text-blue-800">CNPJ:</strong> {{ operadora?.cnpj }}</div>
          <div><strong class="text-blue-800">UF:</strong> {{ operadora?.uf }}</div>
          <div><strong class="text-blue-800">Reg ANS:</strong> {{ operadora?.reg_ans }}</div>
        </div>

        <h4 class="font-bold text-lg mb-3 border-b pb-2">Histórico de Despesas</h4>

        <div v-if="loading" class="text-center py-4 text-blue-600">Carregando...</div>
        <ul v-else-if="despesas.length > 0" class="space-y-2">
          <li v-for="(d, index) in despesas" :key="index" class="p-3 bg-gray-50 rounded flex justify-between border">
            <span>📅 {{ d.trimestre }}º Tri / {{ d.ano }}</span>
            <span class="font-bold text-green-600">R$ {{ Number(d.valor).toLocaleString('pt-BR', {minimumFractionDigits: 2}) }}</span>
          </li>
        </ul>
        <div v-else class="text-center text-gray-500 py-4 italic">Nenhuma despesa encontrada.</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { api } from '../services/api';

const props = defineProps(['aberto', 'operadora']);
defineEmits(['close']);

const despesas = ref([]);
const loading = ref(false);

watch(() => props.operadora, async (newVal) => {
  if (newVal && props.aberto) {
    loading.value = true;
    despesas.value = [];
    try {
      despesas.value = await api.getDespesas(newVal.cnpj);
    } catch (e) {
      console.error(e);
    } finally {
      loading.value = false;
    }
  }
});
</script>