<template>
  <div class="bg-white p-6 rounded-lg shadow-md mb-8">
    <h2 class="text-xl font-bold mb-4 text-gray-700">Distribuição de Despesas por UF (Top 5)</h2>
    <div class="h-64 relative w-full">
      <canvas id="graficoDespesas"></canvas>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import Chart from 'chart.js/auto';
import { api } from '../services/api';

let chartInstance = null;

onMounted(async () => {
  try {
    const data = await api.getEstatisticas();
    if (!data.distribuicao_uf) return;

    const estados = data.distribuicao_uf.map(item => item.uf);
    const valores = data.distribuicao_uf.map(item => item.total);

    const ctx = document.getElementById('graficoDespesas');
    if (chartInstance) chartInstance.destroy();

    chartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: estados,
        datasets: [{
          label: 'Total de Despesas (R$)',
          data: valores,
          backgroundColor: '#3B82F6',
          borderRadius: 4
        }]
      },
      options: { responsive: true, maintainAspectRatio: false }
    });
  } catch (e) {
    console.error("Erro ao carregar gráfico", e);
  }
});
</script>