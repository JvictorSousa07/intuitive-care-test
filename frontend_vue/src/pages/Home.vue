<template>
  <div class="max-w-6xl mx-auto p-6 bg-gray-50 min-h-screen">
    <header class="mb-8 border-b pb-4">
      <h1 class="text-3xl font-bold text-blue-900">Portal ANS</h1>
      <p class="text-gray-500">Consulta de Operadoras e Despesas</p>
    </header>

    <GraficoUF />

    <div class="bg-white p-6 rounded-lg shadow-md mb-6 flex gap-4">
      <input v-model="busca" @keyup.enter="buscar(1)" type="text"
             placeholder="Pesquise por Razão Social ou CNPJ..."
             class="flex-1 border p-3 rounded-lg outline-none focus:ring-2 focus:ring-blue-500">
      <button @click="buscar(1)" class="bg-blue-600 text-white font-bold px-8 rounded-lg hover:bg-blue-700 transition">
        Buscar
      </button>
    </div>

    <div class="bg-white rounded-lg shadow-md overflow-hidden">
      <table class="w-full text-left">
        <thead class="bg-gray-200 text-gray-700">
        <tr>
          <th class="p-4">Reg. ANS</th>
          <th class="p-4">CNPJ</th>
          <th class="p-4">Razão Social</th>
          <th class="p-4 text-center">Ação</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="op in operadoras" :key="op.reg_ans" class="hover:bg-blue-50 border-b transition">
          <td class="p-4 text-blue-600 font-mono">{{ op.reg_ans }}</td>
          <td class="p-4 text-sm text-gray-600">{{ op.cnpj }}</td>
          <td class="p-4 font-medium">{{ op.razao_social }}</td>
          <td class="p-4 text-center">
            <button @click="abrirModal(op)" class="text-blue-600 hover:text-blue-800 font-bold text-sm underline">Ver Detalhes</button>
          </td>
        </tr>
        <tr v-if="operadoras.length === 0">
          <td colspan="4" class="p-6 text-center text-gray-500">Nenhum dado encontrado.</td>
        </tr>
        </tbody>
      </table>

      <div class="p-4 bg-gray-50 flex justify-between items-center border-t">
        <button @click="mudarPagina(-1)" :disabled="page <= 1" class="px-4 py-2 bg-white border rounded hover:bg-gray-100 disabled:opacity-50">⬅ Anterior</button>
        <span class="text-gray-600">Página {{ page }} de {{ totalPages }}</span>
        <button @click="mudarPagina(1)" :disabled="page >= totalPages" class="px-4 py-2 bg-white border rounded hover:bg-gray-100 disabled:opacity-50">Próxima ➡</button>
      </div>
    </div>

    <OperadoraModal :aberto="modalAberto" :operadora="selecionada" @close="modalAberto = false" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from '../services/api';
import GraficoUF from '../components/GraficoUF.vue';
import OperadoraModal from '../components/OperadoraModal.vue';

const operadoras = ref([]);
const page = ref(1);
const totalPages = ref(1);
const busca = ref('');
const modalAberto = ref(false);
const selecionada = ref(null);

const buscar = async (pg = 1) => {
  page.value = pg;
  const data = await api.listarOperadoras(page.value, 10, busca.value);
  if (data && data.data) {
    operadoras.value = data.data;
    totalPages.value = data.meta.pages;
  }
};

const mudarPagina = (delta) => {
  const nova = page.value + delta;
  if (nova >= 1 && nova <= totalPages.value) buscar(nova);
};

const abrirModal = (op) => {
  selecionada.value = op;
  modalAberto.value = true;
};

onMounted(() => buscar());
</script>